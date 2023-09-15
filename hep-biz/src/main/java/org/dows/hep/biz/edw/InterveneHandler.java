package org.dows.hep.biz.edw;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.edw.HepOperateEntity;
import org.dows.edw.HepOperateTypeEnum;
import org.dows.edw.repository.HepOperateGetRepository;
import org.dows.edw.repository.HepOperateSetRepository;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.dows.hep.entity.OperateOrgFuncEntity;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;
import org.dows.hep.properties.MongoProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author fhb
 * @version 1.0
 * @description 使用 mongo 进行干预类的保存和查询
 * @date 2023/9/14 16:21
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class InterveneHandler {
    // mongo
    private final HepOperateGetRepository hepOperateGetRepository;
    private final HepOperateSetRepository hepOperateSetRepository;

    // properties
    private final MongoProperties mongoProperties;

    /**
     * @param rowOrgFunc     - 功能点
     * @param rowOrgFuncSnap - 快照
     * @param supplier       - 回调
     * @param clazz          - 操作对象
     * @return boolean
     * @author fhb
     * @description useMongo ? mongo : mysql
     * @date 2023/9/15 11:34
     */
    public <T extends HepOperateEntity> boolean write(OperateOrgFuncEntity rowOrgFunc, List<OperateOrgFuncSnapEntity> rowOrgFuncSnap, Supplier<Boolean> supplier, Class<T> clazz) {
        boolean useMongo = mongoProperties != null && mongoProperties.getEnable() != null && mongoProperties.getEnable();
        if (useMongo) {
            JSONObject data = new JSONObject();
            data.put("rowOrgFunc", rowOrgFunc);
            data.put("rowOrgFuncSnap", rowOrgFuncSnap);
            HepOperateSetRequest hepOperateSetRequest = HepOperateSetRequest.builder()
                    .type(HepOperateTypeEnum.getNameByCode(clazz))
                    .experimentInstanceId(Long.valueOf(rowOrgFunc.getExperimentInstanceId()))
                    .experimentGroupId(Long.valueOf(rowOrgFunc.getExperimentGroupId()))
                    .operatorId(Long.valueOf(rowOrgFunc.getOperateAccountId()))
                    .orgTreeId(Long.valueOf(rowOrgFunc.getExperimentOrgId()))
                    .flowId(String.valueOf(rowOrgFunc.getOperateFlowId()))
                    .personId(Long.valueOf(rowOrgFunc.getExperimentPersonId()))
                    .orgName(null)
                    .functionName(null)
                    .functionCode(rowOrgFunc.getIndicatorFuncId())
                    .data(data.toString())
                    .period(rowOrgFunc.getPeriods())
                    .onDate(null)
                    .onDay(rowOrgFunc.getOperateGameDay())
                    .build();
            Object operateEntity = hepOperateSetRepository.setOperateEntity(hepOperateSetRequest, clazz);
            return BeanUtil.isNotEmpty(operateEntity);
        }

        return supplier.get();
    }

    /**
     * @param request - 请求参数
     * @param func    - 回调
     * @param clazz   - 操作对象
     * @return java.util.List<org.dows.hep.entity.OperateOrgFuncSnapEntity>
     * @author fhb
     * @description useMongo ? mongo : mysql
     * @date 2023/9/15 11:38
     */
    public <T extends HepOperateEntity> List<OperateOrgFuncSnapEntity> read(ExptOperateOrgFuncRequest request, Function<ExptOperateOrgFuncRequest, List<OperateOrgFuncSnapEntity>> func, Class<T> clazz) {
        boolean useMongo = mongoProperties != null && mongoProperties.getEnable() != null && mongoProperties.getEnable();
        if (useMongo) {
            HepOperateGetRequest hepOperateGetRequest = HepOperateGetRequest.builder()
                    .type(HepOperateTypeEnum.getNameByCode(clazz))
                    .experimentInstanceId(Long.valueOf(request.getExperimentInstanceId()))
                    .experimentGroupId(Long.valueOf(request.getExperimentGroupId()))
//                    .operatorId(Long.valueOf(reqOperateFunc.getOperateAccountId()))
                    .orgTreeId(Long.valueOf(request.getExperimentOrgId()))
                    .flowId(String.valueOf(request.getOperateFlowId()))
                    .personId(Long.valueOf(request.getExperimentPersonId()))
                    .period(request.getPeriods())
                    .build();
            T operateEntity = hepOperateGetRepository.getOperateEntity(hepOperateGetRequest, clazz);
            if (BeanUtil.isEmpty(operateEntity)) {
                return null;
            }
            String data = operateEntity.getData();
            if (StrUtil.isBlank(data)) {
                return null;
            }
            JSONObject jsonObject = JSONUtil.parseObj(data);
            if (jsonObject == null) {
                return null;
            }
            OperateOrgFuncEntity rowOrgFunc = (OperateOrgFuncEntity) jsonObject.get("rowOrgFunc");
            if (rowOrgFunc == null) {
                return null;
            }
            List<OperateOrgFuncSnapEntity> rowOrgFuncSnap = (List<OperateOrgFuncSnapEntity>) jsonObject.get("owOrgFuncSnap");
            return rowOrgFuncSnap;
        }

        return func.apply(request);
    }
}
