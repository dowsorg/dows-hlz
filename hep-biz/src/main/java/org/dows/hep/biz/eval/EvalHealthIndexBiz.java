package org.dows.hep.biz.eval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.ExperimentRsCalculateAndCreateReportHealthScoreRequestRs;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.*;
import org.dows.hep.biz.base.indicator.RsExperimentCrowdsBiz;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorExpressionBiz;
import org.dows.hep.biz.base.indicator.RsExperimentIndicatorInstanceBiz;
import org.dows.hep.biz.base.indicator.RsUtilBiz;
import org.dows.hep.biz.calc.RiskFactorScoreVO;
import org.dows.hep.biz.calc.RiskModelHealthIndexVO;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.eval.data.EvalRiskValues;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.dows.hep.service.ExperimentPersonHealthRiskFactorRsService;
import org.dows.hep.service.ExperimentPersonRiskModelRsService;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/8/18 13:40
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class EvalHealthIndexBiz {

    private final ExperimentPersonService experimentPersonService;

    private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;

    private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;

    private final RsExperimentCrowdsBiz rsExperimentCrowdsBiz;

    private final ExperimentIndicatorValRsService experimentIndicatorValRsService;

    private final ExperimentPersonRiskModelRsService experimentPersonRiskModelRsService;

    private final ExperimentPersonHealthRiskFactorRsService experimentPersonHealthRiskFactorRsService;

    private final IdGenerator idGenerator;

    private final PersonIndicatorIdCache personIndicatorIdCache;
    private final ExperimentPersonCache experimentPersonCache;

    private final EvalPersonCache evalPersonCache;

    private final EvalPersonDao evalPersonDao;

    private final EvalHealthIndexAdvBiz evalHealthIndexAdvBiz;

    //region new
    public void evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req)  {
        if(ConfigExperimentFlow.SWITCH2SpelCache){
            evalHealthIndexAdvBiz.evalPersonHealthIndex(req);
            return;
        }

        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb,"EVALTRACE--evalHP--");
        try {
            String appId = req.getAppId();
            Integer originPeriods = req.getPeriods();
            Integer periods = Math.max(1, originPeriods);
            String experimentId = req.getExperimentId();
            Set<String> experimentPersonIdSet = experimentPersonCache.getPersondIdSet(experimentId, req.getExperimentPersonIds());
            if (ShareUtil.XObject.isEmpty(experimentPersonIdSet)) {
                return;
            }
            ts=logCostTime(sb,"1-personid", ts);
            List<ExperimentPersonRiskModelRsEntity> experimentPersonRiskModelRsEntityList = new ArrayList<>();
            List<ExperimentPersonHealthRiskFactorRsEntity> experimentPersonHealthRiskFactorRsEntityList = new ArrayList<>();
       /* Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
        rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap(kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentPersonIdSet);
*/
            Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap = new HashMap<>();
            rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap(
                    kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap, experimentId
            );
            if (kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.isEmpty()) {
                log.error("RsExperimentCalculateBiz.experimentRsCalculateAndCreateReportHealthScore Crowds is empty experimentId:{}", experimentId);
                return;
            }
            ts=logCostTime(sb,"2-crowds", ts);
            Set<String> experimentCrowdsIdSet = new HashSet<>();
            List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList = new ArrayList<>();
            kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.forEach((experimentCrowdsId, experimentCrowdsInstanceRsEntity) -> {
                experimentCrowdsIdSet.add(experimentCrowdsId);
                experimentCrowdsInstanceRsEntityList.add(experimentCrowdsInstanceRsEntity);
            });
            experimentCrowdsInstanceRsEntityList.sort(Comparator.comparing(ExperimentCrowdsInstanceRsEntity::getDt));
            ts=logCostTime(sb,"3-crowds", ts);

            Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap = new HashMap<>();
            rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap, experimentCrowdsIdSet);
            ts=logCostTime(sb,"4-reason", ts);

            Set<String> crowdsExperimentIndicatorExpressionIdSet = new HashSet<>();
            kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.forEach((experimentReasonId, experimentIndicatorExpressionRefList) -> {
                crowdsExperimentIndicatorExpressionIdSet.addAll(experimentIndicatorExpressionRefList.stream().map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId).collect(Collectors.toSet()));
            });
            Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
            rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, crowdsExperimentIndicatorExpressionIdSet);
            ts=logCostTime(sb,"5-expression", ts);

            Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = new HashMap<>();
            rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap(kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap, experimentCrowdsIdSet);
            ts=logCostTime(sb,"6-riskmodel", ts);

            Set<String> experimentRiskModelIdSet = new HashSet<>();
            kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.forEach((experimentCrowdsId, experimentRiskModelRsEntityList) -> {
                experimentRiskModelIdSet.addAll(experimentRiskModelRsEntityList.stream().map(ExperimentRiskModelRsEntity::getExperimentRiskModelId).collect(Collectors.toSet()));
            });

            if (experimentRiskModelIdSet.isEmpty()) {
                log.error("RsExperimentCalculateBiz.experimentRsCalculateAndCreateReportHealthScore riskModel is empty experimentId:{}", experimentId);
                return;
            }
            Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap = new HashMap<>();
            rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap, experimentRiskModelIdSet);
            ts=logCostTime(sb,"6-riskexpression", ts);

            Set<String> riskModelExperimentIndicatorExpressionIdSet = new HashSet<>();
            kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.forEach((riskModelExperimentReasonId, experimentIndicatorExpressionRefList) -> {
                riskModelExperimentIndicatorExpressionIdSet.addAll(experimentIndicatorExpressionRefList.stream().map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId).collect(Collectors.toSet()));
            });
            Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
            rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, riskModelExperimentIndicatorExpressionIdSet);
            ts=logCostTime(sb,"7-riskexpressionitem", ts);

            Map<String, ExperimentIndicatorExpressionRsEntity> kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
            rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap, riskModelExperimentIndicatorExpressionIdSet);
            ts=logCostTime(sb,"8-indicatorexpression", ts);

            Set<String> riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet = new HashSet<>();
            kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.values().forEach(experimentIndicatorExpressionRsEntity -> {
                String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
                if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
                    riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
                }
                String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
                if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
                    riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
                }
            });
            Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
            rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet);
            ts=logCostTime(sb,"9-indicatorexpressionitem", ts);

            final int CONCURRENTNum = 1;

            if (CONCURRENTNum <= 1 || experimentPersonIdSet.size() < CONCURRENTNum) {
                evalPersonHealthIndex(req, experimentId, experimentPersonIdSet,
                        experimentCrowdsInstanceRsEntityList,
                        kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap,
                        kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                        kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap,
                        kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap,
                        kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap,
                        kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                        kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap);
            } else {
                List<List<String>> groups = ShareUtil.XCollection.split(List.copyOf(experimentPersonIdSet), CONCURRENTNum);
                CompletableFuture[] futures = new CompletableFuture[groups.size()];
                int pos = 0;
                for (List<String> personIds : groups) {
                    futures[pos++] = CompletableFuture.runAsync(() -> evalPersonHealthIndex(req, experimentId, personIds,
                                    experimentCrowdsInstanceRsEntityList,
                                    kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap,
                                    kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                    kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap,
                                    kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap,
                                    kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap,
                                    kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap),
                            EvalPersonExecutor.Instance().getThreadPool());
                }
                CompletableFuture.allOf(futures).join();
            }
            ts=logCostTime(sb,"10-evalend", ts);
        }finally {
            log.info(sb.toString());
            log.error(sb.toString());
            sb.setLength(0);
        }

    }

    public void evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req, String experimentId, Collection<String> experimentPersonIds,
                                              List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList,
                                              Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap,
                                              Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                              Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap,
                                              Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap,
                                              Map<String, ExperimentIndicatorExpressionRsEntity> kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap,
                                              Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                              Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap){

        RiskSavePack savePack=new RiskSavePack();
        for(String personId:experimentPersonIds){
            evalPersonHealthIndex(savePack,req, experimentId, personId,
                    experimentCrowdsInstanceRsEntityList,
                    kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap,
                    kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                    kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap,
                    kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap,
                    kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap,
                    kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                    kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap);
        }
        evalPersonDao.saveRisks(savePack.getExperimentPersonRiskModelRsEntityList(),savePack.getExperimentPersonHealthRiskFactorRsEntityList());
    }

    public void evalPersonHealthIndex(RiskSavePack rst,ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req, String experimentId, String experimentPersonId,
                                      List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList,
                                      Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap,
                                      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                      Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap,
                                      Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap,
                                      Map<String, ExperimentIndicatorExpressionRsEntity> kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap,
                                      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
                                      Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap) {

        final boolean isNewPeriod = EnumEvalFuncType.isNewPeriod(req.getFuncType());
        Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = personIndicatorIdCache.getMapBaseCase2ExptId(experimentPersonId);
        if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap) || kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.isEmpty()) {
            return;
        }

        final EvalPersonPointer evalPointer = evalPersonCache.getPointer(experimentId, experimentPersonId);
        final EvalPersonOnceHolder evalHolder = evalPointer.getCurHolder();
        Map<String, ExperimentIndicatorValRsEntity> mapCurVal = evalHolder.get().getOldMap(true);
        //Map<String, ExperimentIndicatorValRsEntity> mapLastVal=evalHolder.getLastHolder().get().getOldMap();

        final List<RiskModelHealthIndexVO> vosHealthIndex = new ArrayList<>();
        final List<EvalRiskValues> voRisks = new ArrayList<>();
        AtomicReference<String> crowdsAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);

        experimentCrowdsInstanceRsEntityList.forEach(experimentCrowdsInstanceRsEntity -> {
            crowdsAR.set(RsUtilBiz.RESULT_DROP);
            String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
            List<ExperimentIndicatorExpressionRefRsEntity> crowdsExperimentIndicatorExpressionRefRsEntityList = kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get(experimentCrowdsId);
            if (Objects.isNull(crowdsExperimentIndicatorExpressionRefRsEntityList) || crowdsExperimentIndicatorExpressionRefRsEntityList.isEmpty()) {
                return;
            }

            ExperimentIndicatorExpressionRefRsEntity crowdsExperimentIndicatorExpressionRefRsEntity = crowdsExperimentIndicatorExpressionRefRsEntityList.get(0);
            String crowdsIndicatorExpressionId = crowdsExperimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
            List<ExperimentIndicatorExpressionItemRsEntity> crowdsExperimentIndicatorExpressionItemRsEntityList = kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(crowdsIndicatorExpressionId);
            if (Objects.isNull(crowdsExperimentIndicatorExpressionItemRsEntityList) || crowdsExperimentIndicatorExpressionItemRsEntityList.isEmpty()) {
                return;
            }
            rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                    EnumIndicatorExpressionField.EXPERIMENT.getField(),
                    EnumIndicatorExpressionSource.CROWDS.getSource(),
                    EnumIndicatorExpressionScene.EXPERIMENT_CALCULATE_HEALTH_POINT.getScene(),
                    crowdsAR,
                    kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
                    DatabaseCalIndicatorExpressionRequest.builder().build(),
                    CaseCalIndicatorExpressionRequest.builder().build(),
                    ExperimentCalIndicatorExpressionRequest
                            .builder()
                            .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(mapCurVal)
                            .experimentIndicatorExpressionItemRsEntityList(crowdsExperimentIndicatorExpressionItemRsEntityList)
                            .build()
            );
            if (StringUtils.equals(RsUtilBiz.RESULT_DROP, crowdsAR.get())
                    || !StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), crowdsAR.get())) {
                return;
            }

            List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get(experimentCrowdsId);
            if (Objects.isNull(experimentRiskModelRsEntityList) || experimentRiskModelRsEntityList.isEmpty()) {
                return;
            }

            experimentRiskModelRsEntityList.forEach(experimentRiskModelRsEntity -> {
                String experimentPersonRiskModelId = idGenerator.nextIdStr();
                String experimentRiskModelId = experimentRiskModelRsEntity.getExperimentRiskModelId();


                List<ExperimentIndicatorExpressionRefRsEntity> riskModelExperimentIndicatorExpressionRefRsEntityList = kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.get(experimentRiskModelId);
                if (Objects.isNull(riskModelExperimentIndicatorExpressionRefRsEntityList) || riskModelExperimentIndicatorExpressionRefRsEntityList.isEmpty()) {
                    return;
                }
                final List<RiskFactorScoreVO> vosFactorScore = new ArrayList<>();


                riskModelExperimentIndicatorExpressionRefRsEntityList.forEach(riskModelExperimentIndicatorExpressionRefRsEntity -> {
                    String riskModelIndicatorExpressionId = riskModelExperimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
                    ExperimentIndicatorExpressionRsEntity riskModelExperimentIndicatorExpressionRsEntity = kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get(riskModelIndicatorExpressionId);
                    if (Objects.isNull(riskModelExperimentIndicatorExpressionRsEntity)) {
                        return;
                    }
                    String indicatorInstanceId = riskModelExperimentIndicatorExpressionRsEntity.getPrincipalId();
                    String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
                    AtomicReference<String> singleExpressionResultRiskModelAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
                    String experimentIndicatorExpressionId = riskModelExperimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
                    List<ExperimentIndicatorExpressionItemRsEntity> riskModelExperimentIndicatorExpressionItemRsEntityList = kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
                    if (Objects.isNull(riskModelExperimentIndicatorExpressionItemRsEntityList) || riskModelExperimentIndicatorExpressionItemRsEntityList.isEmpty()) {
                        return;
                    }
                    String minIndicatorExpressionItemId = riskModelExperimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
                    ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
                    String maxIndicatorExpressionItemId = riskModelExperimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
                    ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
                    rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                            EnumIndicatorExpressionField.EXPERIMENT.getField(), EnumIndicatorExpressionSource.RISK_MODEL.getSource(), EnumIndicatorExpressionScene.EXPERIMENT_CALCULATE_HEALTH_POINT.getScene(),
                            singleExpressionResultRiskModelAR,
                            kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
                            DatabaseCalIndicatorExpressionRequest.builder().build(),
                            CaseCalIndicatorExpressionRequest.builder().build(),
                            ExperimentCalIndicatorExpressionRequest
                                    .builder()
                                    .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(mapCurVal)
                                    .experimentIndicatorExpressionRsEntity(riskModelExperimentIndicatorExpressionRsEntity)
                                    .experimentIndicatorExpressionItemRsEntityList(riskModelExperimentIndicatorExpressionItemRsEntityList)
                                    .minExperimentIndicatorExpressionItemRsEntity(minExperimentIndicatorExpressionItemRsEntity)
                                    .maxExperimentIndicatorExpressionItemRsEntity(maxExperimentIndicatorExpressionItemRsEntity)
                                    .build()
                    );
                    if (StringUtils.equals(RsUtilBiz.RESULT_DROP, singleExpressionResultRiskModelAR.get())) {
                        return;
                    }

                    final BigDecimal curVal = BigDecimalUtil.tryParseDecimalElseNull(singleExpressionResultRiskModelAR.get());
                    vosFactorScore.add(new RiskFactorScoreVO(experimentRiskModelId, riskModelIndicatorExpressionId, curVal,
                            null == minExperimentIndicatorExpressionItemRsEntity ? null : BigDecimalUtil.tryParseDecimalElseNull(minExperimentIndicatorExpressionItemRsEntity.getResultRaw()),
                            null == maxExperimentIndicatorExpressionItemRsEntity ? null : BigDecimalUtil.tryParseDecimalElseNull(maxExperimentIndicatorExpressionItemRsEntity.getResultRaw())));
                    if (isNewPeriod) {
                        rst.experimentPersonHealthRiskFactorRsEntityList.add(ExperimentPersonHealthRiskFactorRsEntity
                                .builder()
                                .experimentPersonHealthRiskFactorId(idGenerator.nextIdStr())
                                .experimentPersonRiskModelId(experimentPersonRiskModelId)
                                .experimentIndicatorInstanceId(experimentIndicatorInstanceId)
                                .name(Optional.ofNullable(personIndicatorIdCache.getIndicatorById(experimentPersonId, experimentIndicatorInstanceId))
                                        .map(ExperimentIndicatorInstanceRsEntity::getIndicatorName)
                                        .orElse(""))
                                .val(Optional.ofNullable(evalHolder.getIndicator(experimentIndicatorInstanceId))
                                        .map(EvalIndicatorValues::getCurVal)
                                        .orElse(""))
                                .riskScore(curVal.doubleValue())
                                .build());
                    }
                });
                final RiskModelHealthIndexVO voRiskModel = new RiskModelHealthIndexVO()
                        .setCrowdsId(experimentCrowdsInstanceRsEntity.getCrowdsId())
                        .setRiskModelId(experimentRiskModelId)
                        .setRiskModelName(experimentRiskModelRsEntity.getName())
                        .setCrowdsDeathRate(experimentCrowdsInstanceRsEntity.getDeathProbability())
                        .setRiskModelDeathRate(experimentRiskModelRsEntity.getRiskDeathProbability())
                        .setRiskFactors(vosFactorScore);
                vosHealthIndex.add(EvalHealthIndexUtil.evalRiskModelHealthIndex(voRiskModel));
                voRisks.add(new EvalRiskValues()
                        .setCrowdId(experimentRiskModelRsEntity.getCrowdsCategoryId())
                        .setRiskId(experimentRiskModelId)
                        .setRiskName(experimentRiskModelRsEntity.getName())
                );

                if (isNewPeriod) {
                    rst.experimentPersonRiskModelRsEntityList.add(ExperimentPersonRiskModelRsEntity
                            .builder()
                            .experimentPersonRiskModelId(experimentPersonRiskModelId)
                            .experimentId(experimentId)
                            .appId(req.getAppId())
                            .periods(req.getPeriods())
                            .experimentPersonId(experimentPersonId)
                            .experimentRiskModelId(experimentRiskModelId)
                            .name(experimentRiskModelRsEntity.getName())
                            .riskDeathProbability(experimentRiskModelRsEntity.getRiskDeathProbability())
                            .composeRiskScore(BigDecimalUtil.doubleValue(voRiskModel.getScore()))
                            .existDeathRiskScore(BigDecimalUtil.doubleValue(voRiskModel.getExistsDeathScore()))
                            .build());
                }
            });
        });
        BigDecimal personHealthIndex = EvalHealthIndexUtil.evalHealthIndex(vosHealthIndex, false);
        evalHolder.get().setEvalRisks(vosHealthIndex).setRisks(voRisks);
        evalHolder.putCurVal(personIndicatorIdCache.getSysIndicatorId(experimentPersonId, EnumIndicatorType.HEALTH_POINT),
                BigDecimalUtil.formatRoundDecimal(personHealthIndex, 2, RoundingMode.HALF_UP), false);
        evalPointer.sync(req.getFuncType());

    }

    //endregion


    //region old
    @Transactional(rollbackFor = Exception.class)
    public void evalPersonHealthIndexOld(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req)  {
        if(ConfigExperimentFlow.SWITCH2EvalCache){
            evalPersonHealthIndex(req);
            return;
        }
        String appId = req.getAppId();
        Integer originPeriods = req.getPeriods();
        Integer periods = Math.max(1, originPeriods);
        String experimentId = req.getExperimentId();
        Set<String> experimentPersonIdSet = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentId)
                .in(ShareUtil.XObject.notEmpty(req.getExperimentPersonIds()), ExperimentPersonEntity::getExperimentPersonId,req.getExperimentPersonIds())
                .select(ExperimentPersonEntity::getExperimentPersonId)
                .list()
                .stream()
                .map(ExperimentPersonEntity::getExperimentPersonId)
                .collect(Collectors.toSet());
        if (experimentPersonIdSet.isEmpty()) {return;}
        final boolean saveRiskFalg=EnumEvalFuncType.isNewPeriod(req.getFuncType());
        List<ExperimentPersonRiskModelRsEntity> experimentPersonRiskModelRsEntityList = new ArrayList<>();
        List<ExperimentPersonHealthRiskFactorRsEntity> experimentPersonHealthRiskFactorRsEntityList = new ArrayList<>();
        Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
        rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap(kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentPersonIdSet);

        Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap = new HashMap<>();
        rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap(
                kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap, experimentId
        );
        if (kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.isEmpty()) {
            log.error("RsExperimentCalculateBiz.experimentRsCalculateAndCreateReportHealthScore Crowds is empty experimentId:{}", experimentId);
            return;
        }
        Set<String> experimentCrowdsIdSet = new HashSet<>();
        List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList = new ArrayList<>();
        kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.forEach((experimentCrowdsId, experimentCrowdsInstanceRsEntity) -> {
            experimentCrowdsIdSet.add(experimentCrowdsId);
            experimentCrowdsInstanceRsEntityList.add(experimentCrowdsInstanceRsEntity);
        });
        experimentCrowdsInstanceRsEntityList.sort(Comparator.comparing(ExperimentCrowdsInstanceRsEntity::getDt));

        Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap  = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap, experimentCrowdsIdSet);

        Set<String> crowdsExperimentIndicatorExpressionIdSet = new HashSet<>();
        kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.forEach((experimentReasonId, experimentIndicatorExpressionRefList) -> {
            crowdsExperimentIndicatorExpressionIdSet.addAll(experimentIndicatorExpressionRefList.stream().map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId).collect(Collectors.toSet()));
        });
        Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, crowdsExperimentIndicatorExpressionIdSet);

        Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = new HashMap<>();
        rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap(kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap, experimentCrowdsIdSet);

        Set<String> experimentRiskModelIdSet = new HashSet<>();
        kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.forEach((experimentCrowdsId, experimentRiskModelRsEntityList) -> {
            experimentRiskModelIdSet.addAll(experimentRiskModelRsEntityList.stream().map(ExperimentRiskModelRsEntity::getExperimentRiskModelId).collect(Collectors.toSet()));
        });

        if (experimentRiskModelIdSet.isEmpty()) {
            log.error("RsExperimentCalculateBiz.experimentRsCalculateAndCreateReportHealthScore riskModel is empty experimentId:{}", experimentId);
            return;
        }
        Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap, experimentRiskModelIdSet);

        Set<String> riskModelExperimentIndicatorExpressionIdSet = new HashSet<>();
        kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.forEach((riskModelExperimentReasonId, experimentIndicatorExpressionRefList) -> {
            riskModelExperimentIndicatorExpressionIdSet.addAll(experimentIndicatorExpressionRefList.stream().map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId).collect(Collectors.toSet()));
        });
        Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, riskModelExperimentIndicatorExpressionIdSet);

        Map<String, ExperimentIndicatorExpressionRsEntity> kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap, riskModelExperimentIndicatorExpressionIdSet);

        Set<String> riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet = new HashSet<>();
        kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.values().forEach(experimentIndicatorExpressionRsEntity -> {
            String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
                riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
            }
            String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
                riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
            }
        });
        Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet);


        Map<String, ExperimentIndicatorValRsEntity> kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateKExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap(kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap, experimentPersonIdSet, periods);


        Map<String, Map<String, ExperimentIndicatorValRsEntity>> kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
        Map<String, String> kExperimentIndicatorIdVNameMap = new HashMap<>();
        rsExperimentIndicatorExpressionBiz.populateWithNameKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, kExperimentIndicatorIdVNameMap, experimentPersonIdSet, periods);

        List<ExperimentIndicatorValRsEntity> healthExperimentIndicatorValRsEntityList = new ArrayList<>();
        kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap.forEach((experimentPersonId, healthExperimentIndicatorValRsEntity) -> {
            AtomicReference<String> crowdsAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
            Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
            if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap) || kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.isEmpty()) {return;}
            /* runsix:这个人这期所有指标值 */
            Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentPersonId);
            if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)) {return;}
            final List<RiskModelHealthIndexVO> vosHealthIndex=new ArrayList<>();
            experimentCrowdsInstanceRsEntityList.forEach(experimentCrowdsInstanceRsEntity -> {
                crowdsAR.set(RsUtilBiz.RESULT_DROP);
                String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
                List<ExperimentIndicatorExpressionRefRsEntity> crowdsExperimentIndicatorExpressionRefRsEntityList = kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get(experimentCrowdsId);
                if (Objects.isNull(crowdsExperimentIndicatorExpressionRefRsEntityList) || crowdsExperimentIndicatorExpressionRefRsEntityList.isEmpty()) {return;}

                /* runsix:因为人群类型只能产生一个公式 */
                ExperimentIndicatorExpressionRefRsEntity crowdsExperimentIndicatorExpressionRefRsEntity = crowdsExperimentIndicatorExpressionRefRsEntityList.get(0);
                String crowdsIndicatorExpressionId = crowdsExperimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
                List<ExperimentIndicatorExpressionItemRsEntity> crowdsExperimentIndicatorExpressionItemRsEntityList = kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(crowdsIndicatorExpressionId);
                if (Objects.isNull(crowdsExperimentIndicatorExpressionItemRsEntityList) || crowdsExperimentIndicatorExpressionItemRsEntityList.isEmpty()) {return;}
                rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                        EnumIndicatorExpressionField.EXPERIMENT.getField(),
                        EnumIndicatorExpressionSource.CROWDS.getSource(),
                        EnumIndicatorExpressionScene.EXPERIMENT_CALCULATE_HEALTH_POINT.getScene(),
                        crowdsAR,
                        kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
                        DatabaseCalIndicatorExpressionRequest.builder().build(),
                        CaseCalIndicatorExpressionRequest.builder().build(),
                        ExperimentCalIndicatorExpressionRequest
                                .builder()
                                .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                                .experimentIndicatorExpressionItemRsEntityList(crowdsExperimentIndicatorExpressionItemRsEntityList)
                                .build()
                );
                if (StringUtils.equals(RsUtilBiz.RESULT_DROP, crowdsAR.get())
                    ||!StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), crowdsAR.get())) {
                    return;
                }

                List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get(experimentCrowdsId);
                if (Objects.isNull(experimentRiskModelRsEntityList) || experimentRiskModelRsEntityList.isEmpty()) {
                    return;
                }

                experimentRiskModelRsEntityList.forEach(experimentRiskModelRsEntity -> {
                    String experimentPersonRiskModelId = idGenerator.nextIdStr();
                    String experimentRiskModelId = experimentRiskModelRsEntity.getExperimentRiskModelId();


                    List<ExperimentIndicatorExpressionRefRsEntity> riskModelExperimentIndicatorExpressionRefRsEntityList = kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.get(experimentRiskModelId);
                    if (Objects.isNull(riskModelExperimentIndicatorExpressionRefRsEntityList) || riskModelExperimentIndicatorExpressionRefRsEntityList.isEmpty()) {
                        return;
                    }
                    final List<RiskFactorScoreVO> vosFactorScore=new ArrayList<>();


                    riskModelExperimentIndicatorExpressionRefRsEntityList.forEach(riskModelExperimentIndicatorExpressionRefRsEntity -> {
                        String riskModelIndicatorExpressionId = riskModelExperimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
                        ExperimentIndicatorExpressionRsEntity riskModelExperimentIndicatorExpressionRsEntity = kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get(riskModelIndicatorExpressionId);
                        if (Objects.isNull(riskModelExperimentIndicatorExpressionRsEntity)) {return;}
                        String indicatorInstanceId = riskModelExperimentIndicatorExpressionRsEntity.getPrincipalId();
                        String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
                        AtomicReference<String> singleExpressionResultRiskModelAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
                        String experimentIndicatorExpressionId = riskModelExperimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
                        List<ExperimentIndicatorExpressionItemRsEntity> riskModelExperimentIndicatorExpressionItemRsEntityList = kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
                        if (Objects.isNull(riskModelExperimentIndicatorExpressionItemRsEntityList) || riskModelExperimentIndicatorExpressionItemRsEntityList.isEmpty()) {return;}
                        String minIndicatorExpressionItemId = riskModelExperimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
                        ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
                        String maxIndicatorExpressionItemId = riskModelExperimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
                        ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
                        rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                                EnumIndicatorExpressionField.EXPERIMENT.getField(), EnumIndicatorExpressionSource.RISK_MODEL.getSource(), EnumIndicatorExpressionScene.EXPERIMENT_CALCULATE_HEALTH_POINT.getScene(),
                                singleExpressionResultRiskModelAR,
                                kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
                                DatabaseCalIndicatorExpressionRequest.builder().build(),
                                CaseCalIndicatorExpressionRequest.builder().build(),
                                ExperimentCalIndicatorExpressionRequest
                                        .builder()
                                        .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                                        .experimentIndicatorExpressionRsEntity(riskModelExperimentIndicatorExpressionRsEntity)
                                        .experimentIndicatorExpressionItemRsEntityList(riskModelExperimentIndicatorExpressionItemRsEntityList)
                                        .minExperimentIndicatorExpressionItemRsEntity(minExperimentIndicatorExpressionItemRsEntity)
                                        .maxExperimentIndicatorExpressionItemRsEntity(maxExperimentIndicatorExpressionItemRsEntity)
                                        .build()
                        );
                        if (StringUtils.equals(RsUtilBiz.RESULT_DROP, singleExpressionResultRiskModelAR.get())) {
                            return;
                        }

                        final BigDecimal curVal = BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get()));
                        vosFactorScore.add(new RiskFactorScoreVO(experimentRiskModelId, riskModelIndicatorExpressionId, curVal,
                               null==minExperimentIndicatorExpressionItemRsEntity?null: BigDecimalUtil.tryParseDecimalElseNull(minExperimentIndicatorExpressionItemRsEntity.getResultRaw()),
                               null==maxExperimentIndicatorExpressionItemRsEntity?null: BigDecimalUtil.tryParseDecimalElseNull(maxExperimentIndicatorExpressionItemRsEntity.getResultRaw())));
                        if(saveRiskFalg) {
                            ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
                            String name = kExperimentIndicatorIdVNameMap.get(experimentIndicatorInstanceId);
                            if (Objects.nonNull(experimentIndicatorValRsEntity) && StringUtils.isNotBlank(name)) {
                                experimentPersonHealthRiskFactorRsEntityList.add(ExperimentPersonHealthRiskFactorRsEntity
                                        .builder()
                                        .experimentPersonHealthRiskFactorId(idGenerator.nextIdStr())
                                        .experimentPersonRiskModelId(experimentPersonRiskModelId)
                                        .experimentIndicatorInstanceId(experimentIndicatorInstanceId)
                                        .name(name)
                                        .val(experimentIndicatorValRsEntity.getCurrentVal())
                                        .riskScore(curVal.doubleValue())
                                        .build());
                            }
                        }
                    });
                    final RiskModelHealthIndexVO voRiskModel=new RiskModelHealthIndexVO()
                            .setRiskModelId(experimentRiskModelId)
                            .setCrowdsDeathRate(experimentCrowdsInstanceRsEntity.getDeathProbability())
                            .setRiskModelDeathRate(experimentRiskModelRsEntity.getRiskDeathProbability())
                            .setRiskFactors(vosFactorScore);
                    vosHealthIndex.add(EvalHealthIndexUtil.evalRiskModelHealthIndex(voRiskModel));

                    if(saveRiskFalg) {
                        experimentPersonRiskModelRsEntityList.add(ExperimentPersonRiskModelRsEntity
                                .builder()
                                .experimentPersonRiskModelId(experimentPersonRiskModelId)
                                .experimentId(experimentId)
                                .appId(appId)
                                .periods(periods)
                                .experimentPersonId(experimentPersonId)
                                .experimentRiskModelId(experimentRiskModelId)
                                .name(experimentRiskModelRsEntity.getName())
                                .riskDeathProbability(experimentRiskModelRsEntity.getRiskDeathProbability())
                                .composeRiskScore(BigDecimalUtil.doubleValue(voRiskModel.getScore()))
                                .existDeathRiskScore(BigDecimalUtil.doubleValue(voRiskModel.getExistsDeathScore()))
                                .build());
                    }
                });

            });
            BigDecimal personHealthIndex= EvalHealthIndexUtil.evalHealthIndex(vosHealthIndex,false);
            healthExperimentIndicatorValRsEntity.setCurrentVal(BigDecimalUtil.formatDecimal(personHealthIndex));
            healthExperimentIndicatorValRsEntityList.add(healthExperimentIndicatorValRsEntity);
        });

        if (!healthExperimentIndicatorValRsEntityList.isEmpty()) {experimentIndicatorValRsService.saveOrUpdateBatch(healthExperimentIndicatorValRsEntityList);}
        if(!saveRiskFalg){
            return;
        }
        if (!experimentPersonRiskModelRsEntityList.isEmpty()) {experimentPersonRiskModelRsService.saveOrUpdateBatch(experimentPersonRiskModelRsEntityList);}
        if (!experimentPersonHealthRiskFactorRsEntityList.isEmpty()) {experimentPersonHealthRiskFactorRsService.saveOrUpdateBatch(experimentPersonHealthRiskFactorRsEntityList);}
    }
    //endregion

    private long logCostTime(StringBuilder sb,String start){
        sb.append(start);
        return System.currentTimeMillis();
    }

    long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs-ts));
        return newTs;
    }

    public static class RiskSavePack {
        @Getter
        private final List<ExperimentPersonRiskModelRsEntity> experimentPersonRiskModelRsEntityList = new ArrayList<>();
        @Getter
        private final List<ExperimentPersonHealthRiskFactorRsEntity> experimentPersonHealthRiskFactorRsEntityList = new ArrayList<>();
    }
}
