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
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.ExperimentSupportExamCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionField;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.biz.base.evaluate.EvaluateBaseBiz;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorExpressionBiz;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorInstanceBiz;
import org.dows.hep.biz.eval.EvalPersonCache;
import org.dows.hep.biz.eval.EvalPersonOnceHolder;
import org.dows.hep.biz.eval.QueryPersonBiz;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.properties.MongoProperties;
import org.dows.hep.service.CaseOrgFeeService;
import org.dows.hep.service.ExperimentOrgService;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.hep.service.OperateInsuranceService;
import org.dows.sequence.api.IdGenerator;
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

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    private final SpelEngine spelEngine;

    private EvaluateBaseBiz baseBiz;

    private final ExperimentPersonService experimentPersonService;

    private final OperateInsuranceService operateInsuranceService;
    private final ExperimentOrgService experimentOrgService;
    private final CaseOrgFeeService caseOrgFeeService;
    private final IdGenerator idGenerator;
    private final OperateCostBiz operateCostBiz;

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
     * @param hepOperateSetRequest     - 功能点
     * @param clazz      1    - 操作对象
     * @author shi
     * @description useMongo ? mongo : mysql
     * @date 2023/9/15 11:34
     */
    public <T extends HepOperateEntity> void write(HepOperateSetRequest hepOperateSetRequest, Class<T> clazz) {

        /*List<JSONObject> list = getItemListFromJson(hepOperateSetRequest.getData());

        Set<String> indicatorInstanceIdSet = new HashSet<>();
        List<JSONObject> examItemList = new ArrayList<>();
        if (Objects.nonNull(list) && !list.isEmpty()) {
            list.forEach(examItem -> {
                indicatorInstanceIdSet.add(examItem.getStr("indicatorInstanceId"));
                examItemList.add(examItem);
            });
        }*/
        AtomicReference<BigDecimal> totalFeeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        totalFeeAtomicReference.set(totalFeeAtomicReference.get().subtract(getTotalFeeFromRequest(hepOperateSetRequest.getData())));

        // 检查实例ID
        String experimentInstanceId = String.valueOf(hepOperateSetRequest.getExperimentInstanceId());
        // 检查人ID
        String personId = String.valueOf(hepOperateSetRequest.getPersonId());
        // 期数
        Integer period = hepOperateSetRequest.getPeriod();
        // 操作人ID
        String operatorId = String.valueOf(hepOperateSetRequest.getOperatorId());
        // 实验机构ID
        String orgTreeId = String.valueOf(hepOperateSetRequest.getOrgTreeId());
        // 流程ID
        String flowId = String.valueOf(hepOperateSetRequest.getFlowId());
        // 小组Id
        String experimentGroupId = String.valueOf(hepOperateSetRequest.getExperimentGroupId());

        /*Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        rsExperimentIndicatorInstanceBiz.populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
                kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,personId, indicatorInstanceIdSet
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

        SpelPersonContext context = new SpelPersonContext().setVariables(personId, null);
        final EvalPersonOnceHolder evalHolder = EvalPersonCache.Instance().getCurHolder(experimentInstanceId,personId);
        examItemList.forEach(examItem -> {
            String indicatorInstanceId = examItem.getStr("indicatorInstanceId");
            String currentVal = evalHolder.getIndicatorVal(indicatorInstanceId,false);
            String unit = null;
            AtomicReference<String> resultExplainAtomicReference = new AtomicReference<>("");

            ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
            if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
                unit = experimentIndicatorInstanceRsEntity.getUnit();
                SpelEvalResult evalRst= spelEngine.loadFromSpelCache().withReasonId(experimentInstanceId, personId,
                                experimentIndicatorInstanceRsEntity.getCaseIndicatorInstanceId(), EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource())
                        .eval(context);
                if(ShareUtil.XObject.notEmpty(evalRst)) {
                    resultExplainAtomicReference.set(evalRst.getValString());
                }
            }
            examItem.set("operateFlowId",hepOperateSetRequest.getFlowId());
            examItem.set("unit",unit);
            examItem.set("currentVal",Optional.ofNullable(BigDecimalOptional.valueOf(resultExplainAtomicReference.get()).getString(2, RoundingMode.HALF_UP))
                    .orElse(currentVal));
        });
        hepOperateSetRequest.setData(JSONUtil.toJsonStr(examItemList));*/
        experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
                .builder()
                .appId(baseBiz.getAppId())
                .experimentId(experimentInstanceId)
                .experimentPersonId(personId)
                .periods(period)
                .moneyChange(totalFeeAtomicReference.get())
                .assertEnough(true)
                .build());

        /*// 获取小组信息
        ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentPersonId,personId)
                .eq(ExperimentPersonEntity::getDeleted,false)
                .one();*/
        //计算每次操作应该返回的报销金额
        BigDecimal reimburse = getExperimentPersonRestitution(totalFeeAtomicReference.get().negate(),personId);
        CostRequest costRequest = CostRequest.builder()
                .operateCostId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentInstanceId)
                .experimentGroupId(experimentGroupId)
                .operatorId(operatorId)
                .experimentOrgId(orgTreeId)
                .operateFlowId(flowId)
                .patientId(personId)
                .feeName(EnumOrgFeeType.FZJCF.getName())
                .feeCode(EnumOrgFeeType.FZJCF.getCode())
                .cost(totalFeeAtomicReference.get().negate())
                .restitution(reimburse)
                .period(period)
                .build();
        operateCostBiz.saveCost(costRequest);
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
     * @param eClass: 调用改接口需要返回的对象类型?如何设计成通用接口
     * @param clazz: 查询mongodb需要的对象类型
     * @return List<ExperimentSupportExamReportResponseRs> 返回的集合对象
     * @author shi
     * @description 查询mongodb返回结果
     * @date 2023/9/17 0:23
     */
    public <T extends HepOperateEntity,E> List<E> read(HepOperateGetRequest hepOperateGetRequest,Class<E> eClass,Class<T> clazz){

        T operateEntity = hepOperateGetRepository.getOperateEntity(hepOperateGetRequest, clazz);
        List<E> list = getItemListFromJson(operateEntity.getData(),eClass);
        return list;
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
        if(children==null){
            BigDecimal fee = jsonObject.getBigDecimal("fee");
            res.add(fee == null ? BigDecimal.ZERO : fee);
            return res;
        }
        for(int i=0;i<children.size();i++){
            JSONObject jsonObject2 = children.getJSONObject(i);
            // 辅助检查二级目录
            JSONArray children2 = jsonObject2.getJSONArray("children");
            if(children2==null){
                BigDecimal fee = jsonObject2.getBigDecimal("fee");
                res.add(fee == null ? BigDecimal.ZERO : fee);
                return res;
            }
            for(int j=0;j<children2.size();j++){
                JSONObject jsonObject3 = children2.getJSONObject(j);
                // 三级目录
                JSONArray children3 = jsonObject3.getJSONArray("children");
                if(children3==null){
                    BigDecimal fee = jsonObject3.getBigDecimal("fee");
                    res.add(fee == null ? BigDecimal.ZERO : fee);
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
    private <T> List<T> getItemListFromJson(String str,Class<T> eClass){

        List<T> list = new ArrayList<>();
        JSONObject jsonObject = JSONUtil.parseObj(str);
        // 一级目录
        JSONArray children = jsonObject.getJSONArray("children");
        if(children == null){
            return null;
        }
        for(int i=0;i<children.size();i++){
            JSONObject jsonObject2 = children.getJSONObject(i);
            // 二级目录
            JSONArray children2 = jsonObject2.getJSONArray("children");
            if(children2 == null){
                list.add(JSONUtil.toBean(jsonObject2,eClass));
            }
            for(int j=0;j<children2.size();j++){
                JSONObject jsonObject3 = children2.getJSONObject(j);
                // 三级目录
                JSONArray children3 = jsonObject3.getJSONArray("children");
                if(children3 == null){
                    list.add(JSONUtil.toBean(jsonObject3,eClass));
                }
                for(int x=0;x<children3.size();x++){
                    JSONObject object4 = children3.getJSONObject(x);
                    list.add(JSONUtil.toBean(object4,eClass));
                }
            }
        }
        return list;
    }
    private BigDecimal getExperimentPersonRestitution(BigDecimal fee,String experimentPersonId){
        //获取在该消费之前的保险购买记录并计算报销比例
        List<OperateInsuranceEntity> insuranceEntityList = operateInsuranceService.lambdaQuery()
                .eq(OperateInsuranceEntity::getExperimentPersonId, experimentPersonId)
                .le(OperateInsuranceEntity::getIndate, new Date())
                .ge(OperateInsuranceEntity::getExpdate, new Date())
                .list();
        //可能会存在多个机构购买情况，金钱要叠加
        BigDecimal reimburse = new BigDecimal(0);
        if (insuranceEntityList != null && insuranceEntityList.size() > 0) {
            for (int j = 0; j < insuranceEntityList.size(); j++) {
                //3.4、通过机构获取报销比例
                ExperimentOrgEntity orgEntity = experimentOrgService.lambdaQuery()
                        .eq(ExperimentOrgEntity::getExperimentOrgId, insuranceEntityList.get(j).getExperimentOrgId())
                        .eq(ExperimentOrgEntity::getDeleted, false)
                        .one();
                if (orgEntity != null && !ReflectUtil.isObjectNull(orgEntity)) {
                    CaseOrgFeeEntity feeEntity = caseOrgFeeService.lambdaQuery()
                            .eq(CaseOrgFeeEntity::getCaseOrgId, orgEntity.getCaseOrgId())
                            .eq(CaseOrgFeeEntity::getFeeCode, "BXF")
                            .one();
                    if (feeEntity != null && !ReflectUtil.isObjectNull(feeEntity)) {
                        reimburse = reimburse.add(fee.multiply(BigDecimal.valueOf(feeEntity.getReimburseRatio())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    }
                }
            }
        }
        return reimburse;
    }
}
