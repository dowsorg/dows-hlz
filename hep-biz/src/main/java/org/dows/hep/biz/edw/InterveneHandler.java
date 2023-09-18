package org.dows.hep.biz.edw;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.dows.edw.HepOperateEntity;
import org.dows.edw.HepOperateTypeEnum;
import org.dows.edw.domain.HepHealthExamination;
import org.dows.edw.repository.HepOperateGetRepository;
import org.dows.edw.repository.HepOperateSetRepository;
import org.dows.hep.api.base.indicator.request.ExperimentSupportExamCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionField;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorExpressionBiz;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorInstanceBiz;
import org.dows.hep.biz.eval.QueryPersonBiz;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.properties.MongoProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;

    private final QueryPersonBiz queryPersonBiz;

    private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;

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
     * @param supportExamCheck     - 功能点
     * @param clazz      1    - 操作对象
     * @author shi
     * @description useMongo ? mongo : mysql
     * @date 2023/9/15 11:34
     */
    public <T extends HepOperateEntity> void write(ExperimentSupportExamCheckRequestRs supportExamCheck, Class<T> clazz) {

        HepOperateSetRequest hepOperateSetRequest = HepOperateSetRequest.builder()
                .type(HepOperateTypeEnum.getNameByCode(clazz))
                .experimentInstanceId(Long.valueOf(supportExamCheck.getExperimentId()))
                .experimentGroupId(Long.valueOf(supportExamCheck.getExperimentGroupId()))
                .operatorId(Long.valueOf(supportExamCheck.getOperatorId()))
                .orgTreeId(Long.valueOf(supportExamCheck.getExperimentOrgId()))
                .flowId(supportExamCheck.getOperateFlowId())
                .personId(Long.valueOf(supportExamCheck.getExperimentPersonId()))
                .orgName(supportExamCheck.getOrgName())
                .functionName(supportExamCheck.getFunctionName())
                .functionCode(supportExamCheck.getIndicatorFuncId())
                .data(supportExamCheck.getData())
                .period(supportExamCheck.getPeriods())
                .onDate(null)
                .onDay(null)
                .build();
        hepOperateSetRepository.setOperateEntity(hepOperateSetRequest, clazz);
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
    /**
     * @param hepOperateGetRequest: 查询mongodb的参数
     //* @param eClass: 调用改接口需要返回的对象类型?如何设计成通用接口
     * @param clazz: 查询mongodb需要的对象类型
     * @return List<ExperimentSupportExamReportResponseRs> 返回的集合对象
     * @author shi
     * @description 查询mongodb返回结果
     * @date 2023/9/17 0:23
     */
    public <T extends HepOperateEntity> List<ExperimentSupportExamReportResponseRs> read(HepOperateGetRequest hepOperateGetRequest,Class<T> clazz){

        T operateEntity = hepOperateGetRepository.getOperateEntity(hepOperateGetRequest, clazz);
        ExperimentSupportExamReportResponseRs experimentSupportExamReportResponseRs = new ExperimentSupportExamReportResponseRs();
        List<ExperimentSupportExamReportResponseRs> experimentSupportExamReportResponseRsList = new ArrayList<>();
        List<JSONObject> list = getItemListFromJson(operateEntity.getData());

        Set<String> indicatorInstanceIdSet = new HashSet<>();

        if (Objects.nonNull(list) && !list.isEmpty()) {
            list.forEach(experimentIndicatorViewSupportExamRsEntity -> {
                indicatorInstanceIdSet.add(experimentIndicatorViewSupportExamRsEntity.getStr("indicatorInstanceId"));
            });
        }
        Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        rsExperimentIndicatorInstanceBiz.populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
                kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, String.valueOf(hepOperateGetRequest.getExperimentInstanceId()), indicatorInstanceIdSet
        );

        Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
        if (!indicatorInstanceIdSet.isEmpty()) {
            indicatorInstanceIdSet.forEach(indicatorInstanceId -> {
                ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
                if (Objects.nonNull(experimentIndicatorInstanceRsEntity) && StringUtils.isNotBlank(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())) {
                    experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                }
            });
        }

        Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap(
                kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap, experimentIndicatorInstanceIdSet
        );

        Set<String> experimentIndicatorExpressionIdSet = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.values()
                .stream().map(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId).collect(Collectors.toSet());
        Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
                kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, experimentIndicatorExpressionIdSet
        );

        Set<String> minAndMaxExperimentIndicatorExpressionItemIdSet = new HashSet<>();
        Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
        kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.values().forEach(experimentIndicatorExpressionRsEntity -> {
            String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
            String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
                minAndMaxExperimentIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
            }
            if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
                minAndMaxExperimentIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
            }
        });
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
                kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, minAndMaxExperimentIndicatorExpressionItemIdSet
        );


        Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap =
                queryPersonBiz.populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
                        (String.valueOf(hepOperateGetRequest.getPersonId()),hepOperateGetRequest.getPeriod());


        list.forEach(experimentIndicatorViewSupportExamRsEntity -> {
                    String currentVal = "";
                    String unit = null;
                    AtomicReference<String> resultExplainAtomicReference = new AtomicReference<>("");
                    String indicatorInstanceId = experimentIndicatorViewSupportExamRsEntity.getStr("indicatorInstanceId");
                    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
                    if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
                        unit = experimentIndicatorInstanceRsEntity.getUnit();
                        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
                        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
                        if (Objects.nonNull(experimentIndicatorValRsEntity)) {
                            currentVal = experimentIndicatorValRsEntity.getCurrentVal();
                            ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.get(experimentIndicatorInstanceId);
                            if (Objects.nonNull(experimentIndicatorExpressionRsEntity)) {
                                String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
                                List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
                                ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = null;
                                ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = null;
                                String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
                                String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
                                if (StringUtils.isNotBlank(minIndicatorExpressionItemId) && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId))) {
                                    minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
                                }
                                if (StringUtils.isNotBlank(maxIndicatorExpressionItemId) && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId))) {
                                    maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
                                }

                                if (Objects.nonNull(experimentIndicatorExpressionItemRsEntityList)) {
                                    rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                                            EnumIndicatorExpressionField.EXPERIMENT.getField(),
                                            EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(),
                                            EnumIndicatorExpressionScene.PHYSICAL_EXAM.getScene(),
                                            resultExplainAtomicReference,
                                            new HashMap<>(),
                                            DatabaseCalIndicatorExpressionRequest.builder().build(),
                                            CaseCalIndicatorExpressionRequest.builder().build(),
                                            ExperimentCalIndicatorExpressionRequest
                                                    .builder()
                                                    .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                                                    .experimentIndicatorExpressionRsEntity(experimentIndicatorExpressionRsEntity)
                                                    .experimentIndicatorExpressionItemRsEntityList(experimentIndicatorExpressionItemRsEntityList)
                                                    .minExperimentIndicatorExpressionItemRsEntity(minExperimentIndicatorExpressionItemRsEntity)
                                                    .maxExperimentIndicatorExpressionItemRsEntity(maxExperimentIndicatorExpressionItemRsEntity)
                                                    .build()
                                    );
                                }
                            }
                        }
                    }
            experimentSupportExamReportResponseRs.setName(experimentIndicatorViewSupportExamRsEntity.getStr("name"));
            experimentSupportExamReportResponseRs.setFee(experimentIndicatorViewSupportExamRsEntity.getBigDecimal("fee"));
            experimentSupportExamReportResponseRs.setCurrentVal(currentVal);
            experimentSupportExamReportResponseRs.setUnit(unit);
            experimentSupportExamReportResponseRs.setResultExplain(experimentIndicatorViewSupportExamRsEntity.getStr("resultAnalysis"));
            experimentSupportExamReportResponseRsList.add(experimentSupportExamReportResponseRs);
        });
        return experimentSupportExamReportResponseRsList;
    }

    /**
     * @param str: json格式的字符串
     * @return BigDecimal 总费用
     * @author shi
     * @description 计算明细数据中总费用
     * @date 2023/9/16 23:20
     */
    private BigDecimal getTotalFeeFromRequest(String str){
        BigDecimal res = new BigDecimal(0);
        // 辅助检查
        JSONObject jsonObject = JSONUtil.parseObj(str);
        // 辅助检查一级目录
        JSONArray children = jsonObject.getJSONArray("children");
        if(children.size()==0){
            return res;
        }
        for(int i=0;i<children.size();i++){
            JSONObject jsonObject2 = children.getJSONObject(i);
            // 辅助检查二级目录
            JSONArray children2 = jsonObject2.getJSONArray("children");
            if(children2.size()==0){
                return res;
            }
            for(int j=0;j<children2.size();j++){
                JSONObject jsonObject3 = children2.getJSONObject(j);
                // 三级目录
                JSONArray children3 = jsonObject3.getJSONArray("children");
                if(children3.size()==0){
                    return res;
                }
                for(int x=0;x<children3.size();x++){
                    BigDecimal fee = children3.getJSONObject(x).getBigDecimal("fee");
                    res.add(fee);
                }
            }
        }
        return res;
    }
    /**
     * @param str:json格式的字符串
     * @return List<JSONObject> 明细数据列表
     * @author shi
     * @description 解析入参获取明细数据
     * @date 2023/9/16 23:27
     */
    private  List<JSONObject> getItemListFromJson(String str){

        List<JSONObject> list = new ArrayList<>();
        JSONObject jsonObject = JSONUtil.parseObj(str);
        // 一级目录
        JSONArray children = jsonObject.getJSONArray("children");
        if(children.size()==0){
            return null;
        }
        for(int i=0;i<children.size();i++){
            JSONObject jsonObject2 = children.getJSONObject(i);
            // 二级目录
            JSONArray children2 = jsonObject2.getJSONArray("children");
            if(children2.size()==0){
                return null;
            }
            for(int j=0;j<children2.size();j++){
                JSONObject jsonObject3 = children2.getJSONObject(j);
                // 三级目录
                JSONArray children3 = jsonObject3.getJSONArray("children");
                if(children3.size()==0){
                    return null;
                }
                for(int x=0;x<children3.size();x++){
                    JSONObject object = children3.getJSONObject(x);
                    list.add(object);
                }
            }
        }
        return list;
    }
}
