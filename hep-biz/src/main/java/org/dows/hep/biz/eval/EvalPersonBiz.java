package org.dows.hep.biz.eval;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorInstanceBiz;
import org.dows.hep.biz.dao.ExperimentEvalLogDao;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.event.PersonBasedEventTask;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/9/7 17:40
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class EvalPersonBiz {

    private final ExperimentEvalLogDao experimentEvalLogDao;
    private final IdGenerator idGenerator;

    private final EvalPersonIndicatorBiz evalPersonIndicatorBiz;

    private final EvalHealthIndexBiz evalHealthIndexBiz;

    private final PersonStatiscBiz personStatiscBiz;

    private final ExperimentScoringBiz experimentScoringBiz;

    private final ExperimentIndicatorViewPhysicalExamRsService experimentIndicatorViewPhysicalExamRsService;

    private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    private final ExperimentPersonService experimentPersonService;

    private final OperateCostBiz operateCostBiz;
    private final OperateInsuranceService operateInsuranceService;
    private final ExperimentOrgService experimentOrgService;
    private final CaseOrgFeeService caseOrgFeeService;
    private final ExperimentIndicatorViewPhysicalExamReportRsService experimentIndicatorViewPhysicalExamReportRsService;

    private final ExperimentIndicatorViewSupportExamRsService experimentIndicatorViewSupportExamRsService;

    private final ExperimentIndicatorViewSupportExamReportRsService experimentIndicatorViewSupportExamReportRsService;

    private final SpelEngine spelEngine;

    private final EvalPersonMoneyBiz evalPersonMoneyBiz;

    public boolean initEvalPersonLog(List<ExperimentIndicatorValRsEntity> src){
        String experimentId="";
        if(!src.isEmpty()){
            experimentId=src.get(0).getExperimentId();
        }
        try {
            final String APPId="3";
            final Date dtNow=new Date();
            Map<String,List<ExperimentIndicatorValRsEntity>> map=new HashMap<>();
            src.forEach(i->map.computeIfAbsent(i.getIndicatorInstance().getExperimentPersonId(), k->new ArrayList<>()).add(i));
            List<ExperimentEvalLogEntity> rowsEval=new ArrayList<>();
            List<ExperimentEvalLogContentEntity> rowsEvalContent=new ArrayList<>();
            map.forEach((k,v)->{
                ExperimentIndicatorValRsEntity first=v.get(0);
                ExperimentEvalLogEntity rowEval=new ExperimentEvalLogEntity()
                        .setAppId(APPId)
                        .setExperimentEvalLogId(idGenerator.nextIdStr())
                        .setExperimentInstanceId(first.getExperimentId())
                        .setExperimentPersonId(first.getIndicatorInstance().getExperimentPersonId())
                        .setEvalNo(0)
                        .setFuncType(0)
                        .setPeriods(1)
                        .setEvalDay(0)
                        .setEvalingTime(dtNow)
                        .setEvaledTime(dtNow)
                        .setLastEvalDay(0);
                List<EvalIndicatorValues> indicators=new ArrayList<>();
                v.forEach(item->{
                    if(EnumIndicatorType.MONEY.getType().equals(item.getIndicatorInstance().getType())){
                        rowEval.setMoney(item.getCurrentVal());
                    }else if(EnumIndicatorType.HEALTH_POINT.getType().equals(item.getIndicatorInstance().getType())){
                        rowEval.setHealthIndex(item.getCurrentVal());
                    }
                    indicators.add(new EvalIndicatorValues()
                            .setEvalNo(0)
                            .setIndicatorId(item.getIndicatorInstanceId())
                            .setIndicatorName(item.getIndicatorInstance().getIndicatorName())
                            .setCurVal(item.getIndicatorInstance().getDef())
                            .setPeriodInitVal(item.getIndicatorInstance().getDef())
                    );
                });
                ExperimentEvalLogContentEntity rowEvalContent=new ExperimentEvalLogContentEntity()
                        .setEvalNo(0)
                        .setExperimentEvalLogId(rowEval.getExperimentEvalLogId())
                        .setExperimentEvalLogContentId(idGenerator.nextIdStr())
                        .setAppId(APPId)
                        .setIndicatorContent(JacksonUtil.toJsonSilence(indicators, true));
                rowsEval.add(rowEval);
                rowsEvalContent.add(rowEvalContent);

            });
            return experimentEvalLogDao.tranSaveBatch(rowsEval, rowsEvalContent, false, true);

        }catch (Exception ex){
            log.error(String.format( "人物初始指标复制失败[id:%s]",experimentId),ex);
            throw ex;
        }

    }

    //体格检查
    @Transactional(rollbackFor = Exception.class)
    public void physicalExamCheck(ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs, HttpServletRequest request) throws ExecutionException, InterruptedException {
        Integer periods = experimentPhysicalExamCheckRequestRs.getPeriods();
        String appId = experimentPhysicalExamCheckRequestRs.getAppId();
        String experimentId = experimentPhysicalExamCheckRequestRs.getExperimentId();
        String experimentPersonId = experimentPhysicalExamCheckRequestRs.getExperimentPersonId();
        String indicatorFuncId = experimentPhysicalExamCheckRequestRs.getIndicatorFuncId();
        String experimentOrgId = experimentPhysicalExamCheckRequestRs.getExperimentOrgId();
        List<String> experimentIndicatorViewPhysicalExamIdList = experimentPhysicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamIdList();
        // 获取人物正在进行的流水号
        String operateFlowId = ShareBiz.assertRunningOperateFlowId(experimentPhysicalExamCheckRequestRs.getAppId(),
                experimentPhysicalExamCheckRequestRs.getExperimentId(),
                experimentPhysicalExamCheckRequestRs.getExperimentOrgId(),
                experimentPhysicalExamCheckRequestRs.getExperimentPersonId());

        Set<String> indicatorInstanceIdSet = new HashSet<>();
        List<ExperimentIndicatorViewPhysicalExamRsEntity> experimentIndicatorViewPhysicalExamRsEntityList = new ArrayList<>();
        if (Objects.nonNull(experimentIndicatorViewPhysicalExamIdList) && !experimentIndicatorViewPhysicalExamIdList.isEmpty()) {
            experimentIndicatorViewPhysicalExamRsService.lambdaQuery()
                    .in(ExperimentIndicatorViewPhysicalExamRsEntity::getExperimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamIdList)
                    .list()
                    .forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
                        indicatorInstanceIdSet.add(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId());
                        experimentIndicatorViewPhysicalExamRsEntityList.add(experimentIndicatorViewPhysicalExamRsEntity);
                    });
        }
        AtomicReference<BigDecimal> totalFeeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        experimentIndicatorViewPhysicalExamRsEntityList.forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
            totalFeeAtomicReference.set(totalFeeAtomicReference.get().subtract(experimentIndicatorViewPhysicalExamRsEntity.getFee()));
        });

        Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        rsExperimentIndicatorInstanceBiz.populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
                kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, experimentPersonId, indicatorInstanceIdSet
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



        List<ExperimentIndicatorViewPhysicalExamReportRsEntity> experimentIndicatorViewPhysicalExamReportRsEntityList = new ArrayList<>();
        SpelPersonContext context = new SpelPersonContext().setVariables(experimentPersonId, null);
        final EvalPersonOnceHolder evalHolder = EvalPersonCache.Instance().getCurHolder(experimentId,experimentPersonId);
        experimentIndicatorViewPhysicalExamRsEntityList.forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
            String indicatorInstanceId = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId();
            String currentVal = "";
            String unit=null;
            int scale=2;
            AtomicReference<String> resultExplainAtomicReference = new AtomicReference<>("");
            ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
            if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
                currentVal=evalHolder.getIndicatorVal(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(),false);
                unit = experimentIndicatorInstanceRsEntity.getUnit();
                scale=getScale(experimentIndicatorInstanceRsEntity.getIndicatorName());
                SpelEvalResult evalRst= spelEngine.loadFromSpelCache().withReasonId(experimentId, experimentPersonId,
                        experimentIndicatorInstanceRsEntity.getCaseIndicatorInstanceId(), EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource())
                        .eval(context);
                if(ShareUtil.XObject.notEmpty(evalRst)) {
                    resultExplainAtomicReference.set(evalRst.getValString());
                }

            }
            ExperimentIndicatorViewPhysicalExamReportRsEntity experimentIndicatorViewPhysicalExamReportRsEntity = ExperimentIndicatorViewPhysicalExamReportRsEntity
                    .builder()
                    .experimentIndicatorViewPhysicalExamReportId(idGenerator.nextIdStr())
                    .experimentId(experimentId)
                    .appId(appId)
                    .period(periods)
                    .indicatorFuncId(indicatorFuncId)
                    .experimentPersonId(experimentPersonId)
                    .operateFlowId(operateFlowId)
                    .name(experimentIndicatorViewPhysicalExamRsEntity.getName())
                    .fee(experimentIndicatorViewPhysicalExamRsEntity.getFee())
                    .currentVal(Optional.ofNullable(BigDecimalOptional.valueOf(resultExplainAtomicReference.get()).getString(scale, RoundingMode.HALF_UP))
                            .orElse(currentVal))
                    .unit(unit)
                    .resultExplain(experimentIndicatorViewPhysicalExamRsEntity.getResultAnalysis())
                    .build();
            experimentIndicatorViewPhysicalExamReportRsEntityList.add(experimentIndicatorViewPhysicalExamReportRsEntity);
        });

        experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .experimentPersonId(experimentPersonId)
                .periods(periods)
                .moneyChange(totalFeeAtomicReference.get())
                .assertEnough(true)
                .build());

        // 保存消费记录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        // 获取小组信息
        ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentPersonId,experimentPhysicalExamCheckRequestRs.getExperimentPersonId())
                .eq(ExperimentPersonEntity::getDeleted,false)
                .one();

        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(personEntity))
                .throwMessage("未找到实验人物");
        //计算每次操作应该返回的报销金额
        BigDecimal reimburse =ShareBiz.getRefundFee(personEntity.getExperimentPersonId(),personEntity.getExperimentInstanceId(),
                LocalDateTime.now(), totalFeeAtomicReference.get().negate());

        //BigDecimal reimburse = getExperimentPersonRestitution(totalFeeAtomicReference.get().negate(),experimentPhysicalExamCheckRequestRs.getExperimentPersonId());
        CostRequest costRequest = CostRequest.builder()
                .operateCostId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentPhysicalExamCheckRequestRs.getExperimentId())
                .experimentGroupId(personEntity.getExperimentGroupId())
                .operatorId(voLogin.getAccountId())
                .experimentOrgId(experimentPhysicalExamCheckRequestRs.getExperimentOrgId())
                .operateFlowId(operateFlowId)
                .patientId(experimentPhysicalExamCheckRequestRs.getExperimentPersonId())
                .feeName(EnumOrgFeeType.TGJCF.getName())
                .feeCode(EnumOrgFeeType.TGJCF.getCode())
                .cost(totalFeeAtomicReference.get().negate())
                .restitution(reimburse)
                .period(experimentPhysicalExamCheckRequestRs.getPeriods())
                .build();
        operateCostBiz.saveCost(costRequest);
        experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamReportRsEntityList);
    }

    //辅助检查
    @Transactional(rollbackFor = Exception.class)
    public void supportExamCheck(ExperimentSupportExamCheckRequestRs experimentSupportExamCheckRequestRs, HttpServletRequest request) throws ExecutionException, InterruptedException {
        String appId = experimentSupportExamCheckRequestRs.getAppId();
        Integer periods = experimentSupportExamCheckRequestRs.getPeriods();
        String experimentId = experimentSupportExamCheckRequestRs.getExperimentId();
        String experimentPersonId = experimentSupportExamCheckRequestRs.getExperimentPersonId();
        String indicatorFuncId = experimentSupportExamCheckRequestRs.getIndicatorFuncId();
        String experimentOrgId = experimentSupportExamCheckRequestRs.getExperimentOrgId();
        List<String> experimentIndicatorViewSupportExamIdList = experimentSupportExamCheckRequestRs.getExperimentIndicatorViewSupportExamIdList();
        // 获取人物正在进行的流水号
        String operateFlowId = ShareBiz.assertRunningOperateFlowId(experimentSupportExamCheckRequestRs.getAppId(),
                experimentSupportExamCheckRequestRs.getExperimentId(),
                experimentSupportExamCheckRequestRs.getExperimentOrgId(),
                experimentSupportExamCheckRequestRs.getExperimentPersonId());

        Set<String> indicatorInstanceIdSet = new HashSet<>();
        List<ExperimentIndicatorViewSupportExamRsEntity> experimentIndicatorViewSupportExamRsEntityList = new ArrayList<>();
        if (Objects.nonNull(experimentIndicatorViewSupportExamIdList) && !experimentIndicatorViewSupportExamIdList.isEmpty()) {
            experimentIndicatorViewSupportExamRsService.lambdaQuery()
                    .in(ExperimentIndicatorViewSupportExamRsEntity::getExperimentIndicatorViewSupportExamId, experimentIndicatorViewSupportExamIdList)
                    .list()
                    .forEach(experimentIndicatorViewSupportExamRsEntity -> {
                        indicatorInstanceIdSet.add(experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId());
                        experimentIndicatorViewSupportExamRsEntityList.add(experimentIndicatorViewSupportExamRsEntity);
                    });
        }
        AtomicReference<BigDecimal> totalFeeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        experimentIndicatorViewSupportExamRsEntityList.forEach(experimentIndicatorViewSupportExamRsEntity -> {
            totalFeeAtomicReference.set(totalFeeAtomicReference.get().subtract(experimentIndicatorViewSupportExamRsEntity.getFee()));
        });

        Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        rsExperimentIndicatorInstanceBiz.populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
                kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, experimentPersonId, indicatorInstanceIdSet
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

        List<ExperimentIndicatorViewSupportExamReportRsEntity> experimentIndicatorViewSupportExamReportRsEntityList = new ArrayList<>();
        SpelPersonContext context = new SpelPersonContext().setVariables(experimentPersonId, null);
        final EvalPersonOnceHolder evalHolder = EvalPersonCache.Instance().getCurHolder(experimentId,experimentPersonId);
        experimentIndicatorViewSupportExamRsEntityList.forEach(experimentIndicatorViewSupportExamRsEntity -> {
            String indicatorInstanceId = experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId();
            String currentVal = "";
            String unit = null;
            int scale=2;
            AtomicReference<String> resultExplainAtomicReference = new AtomicReference<>("");

            ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
            if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
                currentVal=evalHolder.getIndicatorVal(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(),false);
                unit = experimentIndicatorInstanceRsEntity.getUnit();
                scale=getScale(experimentIndicatorInstanceRsEntity.getIndicatorName());
                SpelEvalResult evalRst= spelEngine.loadFromSpelCache().withReasonId(experimentId, experimentPersonId,
                                experimentIndicatorInstanceRsEntity.getCaseIndicatorInstanceId(), EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource())
                        .eval(context);
                if(ShareUtil.XObject.notEmpty(evalRst)) {
                    resultExplainAtomicReference.set(evalRst.getValString());
                }else {
                    resultExplainAtomicReference.set(currentVal);
                }

            }
            ExperimentIndicatorViewSupportExamReportRsEntity experimentIndicatorViewSupportExamReportRsEntity = ExperimentIndicatorViewSupportExamReportRsEntity
                    .builder()
                    .experimentIndicatorViewSupportExamReportId(idGenerator.nextIdStr())
                    .experimentId(experimentId)
                    .appId(appId)
                    .period(periods)
                    .indicatorFuncId(indicatorFuncId)
                    .experimentPersonId(experimentPersonId)
                    .operateFlowId(operateFlowId)
                    .name(experimentIndicatorViewSupportExamRsEntity.getName())
                    .fee(experimentIndicatorViewSupportExamRsEntity.getFee())
                    .currentVal(Optional.ofNullable(BigDecimalOptional.valueOf(resultExplainAtomicReference.get()).getString(scale, RoundingMode.HALF_UP))
                            .orElse(currentVal))
                    .unit(unit)
                    .resultExplain(experimentIndicatorViewSupportExamRsEntity.getResultAnalysis())
                    .build();
            experimentIndicatorViewSupportExamReportRsEntityList.add(experimentIndicatorViewSupportExamReportRsEntity);
        });

        experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .experimentPersonId(experimentPersonId)
                .periods(periods)
                .moneyChange(totalFeeAtomicReference.get())
                .assertEnough(true)
                .build());
        // 保存消费记录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        // 获取小组信息
        ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentPersonId,experimentSupportExamCheckRequestRs.getExperimentPersonId())
                .eq(ExperimentPersonEntity::getDeleted,false)
                .one();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(personEntity))
                .throwMessage("未找到实验人物");
        //计算每次操作应该返回的报销金额
        //BigDecimal reimburse = getExperimentPersonRestitution(totalFeeAtomicReference.get().negate(),experimentSupportExamCheckRequestRs.getExperimentPersonId());
        BigDecimal reimburse =ShareBiz.getRefundFee(personEntity.getExperimentPersonId(),personEntity.getExperimentInstanceId(),
                LocalDateTime.now(), totalFeeAtomicReference.get().negate());
        CostRequest costRequest = CostRequest.builder()
                .operateCostId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentSupportExamCheckRequestRs.getExperimentId())
                .experimentGroupId(personEntity.getExperimentGroupId())
                .operatorId(voLogin.getAccountId())
                .experimentOrgId(experimentSupportExamCheckRequestRs.getExperimentOrgId())
                .operateFlowId(operateFlowId)
                .patientId(experimentSupportExamCheckRequestRs.getExperimentPersonId())
                .feeName(EnumOrgFeeType.FZJCF.getName())
                .feeCode(EnumOrgFeeType.FZJCF.getCode())
                .cost(totalFeeAtomicReference.get().negate())
                .restitution(reimburse)
                .period(experimentSupportExamCheckRequestRs.getPeriods())
                .build();
        operateCostBiz.saveCost(costRequest);
        experimentIndicatorViewSupportExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewSupportExamReportRsEntityList);
    }
    private static final Set<String> INDICTATORNameBloodPressure=Set.of("收缩压","舒张压","心率");
    private int getScale(String indciatorName){
        return INDICTATORNameBloodPressure.contains(indciatorName)?0:2;
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


    /**
     * 功能点结算
     * @param req
     */

    public void evalOrgFunc(RsExperimentCalculateFuncRequest req) {
        StringBuilder sb=new StringBuilder("EVALTRACE--evalFunc--");
        try {
            String appId = req.getAppId();
            String experimentId = req.getExperimentId();
            Integer periods = req.getPeriods();
            Set<String> personIds = Set.of(req.getExperimentPersonId());

            long ts=System.currentTimeMillis();
            evalPersonIndicatorBiz.evalPersonIndicator(RsCalculatePersonRequestRs
                    .builder()
                    .appId(appId)
                    .experimentId(experimentId)
                    .periods(periods)
                    .personIdSet(personIds)
                    .funcType(req.getFuncType())
                    .silence(false)
                    .build());
            ts=logCostTime(sb,"1-indicator",ts);
            evalHealthIndexBiz.evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs
                    .builder()
                    .appId(appId)
                    .experimentId(experimentId)
                    .periods(periods)
                    .experimentPersonIds(personIds)
                    .funcType(req.getFuncType())
                    .build());
            ts=logCostTime(sb,"2-hp",ts);

            PersonBasedEventTask.runPersonBasedEventAsync(appId, experimentId,req.getExperimentPersonId());
        }finally {
            log.error(sb.toString());
        }


    }
    /**
     * 期数翻转
     * @param req
     */
    public void evalPeriodEnd(RsCalculatePeriodsRequest req)  {
        final String appId = req.getAppId();
        final String experimentId = req.getExperimentId();
        final Integer periods = req.getPeriods();

        /*personStatiscBiz.refundFunds(ExperimentPersonRequest.builder()
                .experimentInstanceId(experimentId)
                .appId(appId)
                .periods(periods)
                .build());*/

        evalPersonMoneyBiz.saveRefunds(experimentId, periods, null);

        evalPersonIndicatorBiz.evalPersonIndicator(RsCalculatePersonRequestRs
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .periods(periods)
                .personIdSet(null)
                .funcType(EnumEvalFuncType.PERIODEnd)
                .silence(true)
                .build());
        evalHealthIndexBiz.evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs
                .builder()
                .appId(appId)
                .experimentId(experimentId)
                .periods(periods)
                .funcType(EnumEvalFuncType.PERIODEnd)
                .build());

        experimentScoringBiz.saveOrUpd(experimentId, periods);

        PersonBasedEventTask.runPersonBasedEventAsync(appId,experimentId);

    }

    long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs-ts));
        return newTs;
    }
}
