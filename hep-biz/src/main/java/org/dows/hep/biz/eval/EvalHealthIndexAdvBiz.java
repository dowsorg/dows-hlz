package org.dows.hep.biz.eval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentRsCalculateAndCreateReportHealthScoreRequestRs;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.calc.RiskFactorScoreVO;
import org.dows.hep.biz.calc.RiskModelHealthIndexVO;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.eval.data.EvalRiskValues;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentPersonHealthRiskFactorRsEntity;
import org.dows.hep.entity.ExperimentPersonRiskModelRsEntity;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author : wuzl
 * @date : 2023/8/18 13:40
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class EvalHealthIndexAdvBiz {



    private final IdGenerator idGenerator;

    private final PersonIndicatorIdCache personIndicatorIdCache;
    private final ExperimentPersonCache experimentPersonCache;

    private final EvalPersonCache evalPersonCache;

    private final EvalPersonDao evalPersonDao;

    private final EvalCrowdCache evalCrowdCache;

    private final SpelEngine spelEngine;

    //region
    public void evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req)  {
        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb,"EVALTRACE--evalHP--");
        try {
            final String experimentId = req.getExperimentId();
            Set<String> experimentPersonIdSet = experimentPersonCache.getPersondIdSet(experimentId, req.getExperimentPersonIds());
            if (ShareUtil.XObject.isEmpty(experimentPersonIdSet)) {
                return;
            }
            ts=logCostTime(sb,"1-personid", ts);
            Collection<SnapCrowdsInstanceEntity> crowds=evalCrowdCache.getCrowds(experimentId);
            ts=logCostTime(sb,"2-crowds", ts);

            final int CONCURRENTNum = 1;
            if (CONCURRENTNum <= 1 || experimentPersonIdSet.size() < CONCURRENTNum) {
                evalPersonHealthIndex(req, experimentPersonIdSet,crowds);
            } else {
                List<List<String>> groups = ShareUtil.XCollection.split(List.copyOf(experimentPersonIdSet), CONCURRENTNum);
                CompletableFuture[] futures = new CompletableFuture[groups.size()];
                int pos = 0;
                for (List<String> personIds : groups) {
                    futures[pos++] = CompletableFuture.runAsync(() -> evalPersonHealthIndex(req, personIds,crowds),
                            EvalPersonExecutor.Instance().getThreadPool());
                }
                CompletableFuture.allOf(futures).join();
            }
            ts=logCostTime(sb,"3-end", ts);
        }finally {
            log.info(sb.toString());
            log.error(sb.toString());
            sb.setLength(0);
        }

    }

    public void evalPersonHealthIndex(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req, Collection<String> experimentPersonIds,
                                      Collection<SnapCrowdsInstanceEntity> crowds) {
        RiskSavePack savePack = new RiskSavePack();
        for (String personId : experimentPersonIds) {
            evalPersonHealthIndex(savePack, req, personId,crowds);
        }
        evalPersonDao.saveRisks(savePack.getExperimentPersonRiskModelRsEntityList(), savePack.getExperimentPersonHealthRiskFactorRsEntityList());
    }

    public void evalPersonHealthIndex(RiskSavePack rst,ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req,  String experimentPersonId,
                                      Collection<SnapCrowdsInstanceEntity> crowds) {
        final String experimentId=req.getExperimentId();
        final boolean isNewPeriod = EnumEvalFuncType.isNewPeriod(req.getFuncType());
        final EvalPersonPointer evalPointer = evalPersonCache.getPointer(experimentId, experimentPersonId);
        final EvalPersonOnceHolder evalHolder = evalPointer.getCurHolder();


        final List<RiskModelHealthIndexVO> vosHealthIndex = new ArrayList<>();
        final List<EvalRiskValues> voRisks = new ArrayList<>();
        SpelPersonContext context = new SpelPersonContext().setVariables(experimentPersonId, null);
        //Map<String, SpelEvalSumResult> mapSum=new HashMap<>();
        crowds.forEach(hitCrowd->{
            final String crowdId=hitCrowd.getCrowdsId();
            if(!spelEngine.loadFromSpelCache().withReasonId(experimentId,experimentPersonId,crowdId, EnumIndicatorExpressionSource.CROWDS.getSource())
                    .check(context)){
                return;
            }
            List<SnapRiskModelEntity> hitRiskModels= evalCrowdCache.getRiskModelByCrowdId(experimentId, crowdId);
            if(ShareUtil.XObject.isEmpty(hitRiskModels)){
                return;
            }
            hitRiskModels.forEach(hitRisk-> {
                List<SpelInput> inputs = spelEngine.loadFromSpelCache().withReasonId(experimentId, experimentPersonId, Set.of(hitRisk.getRiskModelId()), EnumIndicatorExpressionSource.RISK_MODEL.getSource())
                        .getInput();
                if (ShareUtil.XObject.isEmpty(inputs)) {
                    return;
                }
                final List<RiskFactorScoreVO> vosFactorScore = new ArrayList<>();
                inputs.forEach(input -> {
                    SpelEvalResult evalRst = spelEngine.loadWith(input).eval(context);
                    if (ShareUtil.XObject.anyEmpty(evalRst, () -> evalRst.getValNumber())) {
                        return;
                    }
                    final BigDecimal riskScore = evalRst.getValNumber();
                    String indicatorName = Optional.ofNullable(personIndicatorIdCache.getIndicatorById(experimentPersonId, input.getIndicatorId()))
                            .map(ExperimentIndicatorInstanceRsEntity::getIndicatorName)
                            .orElse("");
                    vosFactorScore.add(new RiskFactorScoreVO(input.getIndicatorId(), input.getExpressionId(), riskScore,
                            input.getMin(), input.getMax())
                            .setRiskFactorName(indicatorName));
                    if (isNewPeriod) {
                        rst.getExperimentPersonHealthRiskFactorRsEntityList().add(ExperimentPersonHealthRiskFactorRsEntity
                                .builder()
                                .experimentPersonHealthRiskFactorId(idGenerator.nextIdStr())
                                .experimentPersonRiskModelId(input.getReasonId())
                                .experimentIndicatorInstanceId(input.getIndicatorId())
                                .name(indicatorName)
                                .val(Optional.ofNullable(evalHolder.getIndicator(input.getIndicatorId()))
                                        .map(EvalIndicatorValues::getCurVal)
                                        .orElse(""))
                                .riskScore(riskScore.doubleValue())
                                .build());
                    }
                });
                final RiskModelHealthIndexVO voRiskModel = new RiskModelHealthIndexVO()
                        .setCrowdsId(hitCrowd.getCrowdsId())
                        .setRiskModelId(hitRisk.getRiskModelId())
                        .setRiskModelName(hitRisk.getName())
                        .setCrowdsDeathRate(hitCrowd.getDeathProbability())
                        .setRiskModelDeathRate(hitRisk.getRiskDeathProbability())
                        .setRiskFactors(vosFactorScore);
                vosHealthIndex.add(EvalHealthIndexUtil.evalRiskModelHealthIndex(voRiskModel));
                voRisks.add(new EvalRiskValues()
                        .setCrowdId(crowdId)
                        .setRiskId(hitRisk.getRiskModelId())
                        .setRiskName(hitRisk.getName())
                );

                if (isNewPeriod) {
                    rst.getExperimentPersonRiskModelRsEntityList().add(ExperimentPersonRiskModelRsEntity
                            .builder()
                            .experimentPersonRiskModelId(idGenerator.nextIdStr())
                            .experimentId(experimentId)
                            .appId(req.getAppId())
                            .periods(req.getPeriods())
                            .experimentPersonId(experimentPersonId)
                            .experimentRiskModelId(hitRisk.getRiskModelId())
                            .name(hitRisk.getName())
                            .riskDeathProbability(hitRisk.getRiskDeathProbability())
                            .composeRiskScore(BigDecimalUtil.doubleValue(voRiskModel.getScore()))
                            .existDeathRiskScore(BigDecimalUtil.doubleValue(voRiskModel.getExistsDeathScore()))
                            .build());
                }
            });
        });

        BigDecimal personHealthIndex = EvalHealthIndexUtil.evalHealthIndex(vosHealthIndex, false);
        vosHealthIndex.forEach(i->Optional.of(i.getRiskFactors()).ifPresent(v->v.sort(Comparator.comparing(iv->Optional.ofNullable(iv.getRiskFactorName()).orElse("")))));
        evalHolder.get().setEvalRisks(vosHealthIndex).setRisks(voRisks);
        evalHolder.putCurVal(personIndicatorIdCache.getSysIndicatorId(experimentPersonId, EnumIndicatorType.HEALTH_POINT),
                BigDecimalUtil.formatRoundDecimal(personHealthIndex, 2), false);
        evalPointer.sync(req.getFuncType());

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
