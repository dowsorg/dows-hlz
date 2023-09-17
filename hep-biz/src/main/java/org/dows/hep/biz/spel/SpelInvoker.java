package org.dows.hep.biz.spel;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.user.experiment.vo.ExptTreatPlanItemVO;
import org.dows.hep.biz.dao.ExperimentIndicatorInstanceRsDao;
import org.dows.hep.biz.dao.ExperimentIndicatorValRsDao;
import org.dows.hep.biz.eval.EvalPersonCache;
import org.dows.hep.biz.eval.EvalPersonOnceHolder;
import org.dows.hep.biz.spel.meta.SpelCheckResult;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelEvalSumResult;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/7/21 11:39
 */

@Component
@Slf4j
public class SpelInvoker {

    private static volatile SpelInvoker s_instance;

    public static SpelInvoker Instance() {
        return s_instance;
    }

    private SpelInvoker() {
        s_instance = this;
    }

    @Autowired
    private SpelEngine spelEngine;

    @Autowired
    private ExperimentIndicatorValRsDao experimentIndicatorValRsDao;

    @Autowired
    private ExperimentIndicatorInstanceRsDao experimentIndicatorInstanceRsDao;


    //region 治疗干预
    public List<SpelEvalResult> evalTreatEffect(String experimentId, String experimentPersonId, Integer periods,List<ExptTreatPlanItemVO> treatItems,
                                                Map<String, SpelEvalSumResult> mapSum,EnumIndicatorExpressionSource source ) {
        StandardEvaluationContext context = new SpelPersonContext().setVariables(experimentPersonId, periods);
        final Map<String, BigDecimal> mapTreatItem = ShareUtil.XCollection.toMap(treatItems, HashMap::new,ExptTreatPlanItemVO::getTreatItemId,i ->
                BigDecimalUtil.tryParseDecimalElseZero(i.getWeight()),(c,n)->BigDecimalUtil.add(c,n));
        return spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, mapTreatItem.keySet(),source.getSource())
                .prepare(inputs -> inputs.forEach(i -> i.setFactor(mapTreatItem.get(i.getReasonId()))))
                .evalDeltaSum(context, mapSum);
    }
    //endregion

    //region 突发事件
    //触发条件
    public boolean checkEventCondition(String experimentId, String experimentPersonId, String eventId, StandardEvaluationContext context) {
        return spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, eventId,
                        EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource())
                .check(context);
    }
    public List<SpelCheckResult> checkEventCondition(String experimentId, String experimentPersonId,Integer period,Collection<String> eventIds) {
        return spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, eventIds,
                        EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource())
                .check(new SpelPersonContext().setVariables(experimentPersonId, period));
    }

    //事件影响
    public boolean saveEventEffect(String experimentId, String experimentPersonId, Integer periods, Collection<String> eventIds){
        return saveEventEffect(experimentId,experimentPersonId,periods,eventIds,
                new SpelPersonContext().setVariables(experimentPersonId,periods));
    }
    public boolean saveEventEffect(String experimentId, String experimentPersonId, Integer periods, Collection<String> eventIds, StandardEvaluationContext context){
        Map<String, SpelEvalSumResult> mapSum=new HashMap<>();
        List<SpelEvalResult> evalResults= spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, eventIds,
                        EnumIndicatorExpressionSource.EMERGENCY_INFLUENCE_INDICATOR.getSource())
                .evalDeltaSum(context,mapSum);
        return saveIndicator(experimentPersonId,evalResults,mapSum.values(),periods);

    }

    //处理措施作用

    public List<SpelEvalResult> evalEventAction(String experimentId, String experimentPersonId, Integer periods,Collection<String> actionIds,Map<String, SpelEvalSumResult> mapSum){
        return  spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, actionIds,
                        EnumIndicatorExpressionSource.EMERGENCY_ACTION_INFLUENCE_INDICATOR.getSource())
                .evalDeltaSum(new SpelPersonContext().setVariables(experimentPersonId,periods), mapSum);
    }
    public boolean saveEventAction(String experimentId, String experimentPersonId, Integer periods,Collection<String> actionIds){
        return saveEventAction(experimentId,experimentPersonId,periods,actionIds,
                new SpelPersonContext().setVariables(experimentPersonId,periods));
    }
    public boolean saveEventAction(String experimentId, String experimentPersonId, Integer periods,Collection<String> actionIds,StandardEvaluationContext context) {
        Map<String, SpelEvalSumResult> mapSum = new HashMap<>();
        List<SpelEvalResult> evalResults = spelEngine.loadFromSpelCache()
                .withReasonId(experimentId, experimentPersonId, actionIds,
                        EnumIndicatorExpressionSource.EMERGENCY_ACTION_INFLUENCE_INDICATOR.getSource())
                .evalDeltaSum(context, mapSum);
        return saveIndicator(experimentPersonId,evalResults,mapSum.values(), periods);
    }
    //endregion

    //region save

    public boolean saveIndicator(String experimentPersonId,Collection<SpelEvalResult> evalResults, Collection<SpelEvalSumResult> evalSumResults,Integer periods){
        log.info("IndicatorDebug..."+JSONUtil.toJsonStr(evalResults));
        return saveIndicatorChange(experimentPersonId, evalSumResults);
    }


    public boolean saveIndicatorChange(String experimentPersonId,Collection<SpelEvalSumResult> evalResults) {
        if (ShareUtil.XObject.isEmpty(evalResults)) {
            return true;
        }

        if(ConfigExperimentFlow.SWITCH2EvalCache){
            EvalPersonOnceHolder evalHolder= EvalPersonCache.Instance().getCurHolder(experimentPersonId);
            evalResults.forEach(i->evalHolder.putChangeVal(i.getExperimentIndicatorId(), i.getValDecimal(), true));
            return true;
        }else {
            final Map<String, Double> mapEval = ShareUtil.XCollection.toMap(evalResults, SpelEvalSumResult::getExperimentIndicatorId, SpelEvalSumResult::getValDouble);
            List<ExperimentIndicatorInstanceRsEntity> rows = experimentIndicatorInstanceRsDao.getByExperimentIndicatorIds(mapEval.keySet(),
                    ExperimentIndicatorInstanceRsEntity::getId,
                    ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId,
                    ExperimentIndicatorInstanceRsEntity::getChangeVal);
            rows.forEach(i -> i.setChangeVal(
                    Optional.ofNullable(i.getChangeVal()).orElse(0d)
                            + Optional.ofNullable(mapEval.get(i.getExperimentIndicatorInstanceId())).orElse(0d))
            );
            return experimentIndicatorInstanceRsDao.updateIndicatorChange(rows);
        }
    }


    public boolean saveIndicatorCurrent(String experimentPersonId,Collection<SpelEvalResult> evalResults, Collection<SpelEvalSumResult> evalSumResults){
        if(ShareUtil.XObject.isEmpty(evalSumResults)) {
            return true;
        }
        EvalPersonOnceHolder evalHolder= EvalPersonCache.Instance().getCurHolder(experimentPersonId);
        Map<String,String> mapVals=ShareUtil.XCollection.toMap(evalSumResults, SpelEvalSumResult::getExperimentIndicatorId,SpelEvalSumResult::getValString);
        evalSumResults.forEach(i->evalHolder.putCurVal(mapVals, true));

        return true;
    }


    //endregion





}
