package org.dows.hep.biz.spel;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.user.experiment.vo.ExptTreatPlanItemVO;
import org.dows.hep.biz.dao.ExperimentIndicatorInstanceRsDao;
import org.dows.hep.biz.dao.ExperimentIndicatorValRsDao;
import org.dows.hep.biz.spel.meta.SpelCheckResult;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelEvalSumResult;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final boolean DEBUGFlag=true;

    //region 治疗干预
    public List<SpelEvalResult> evalTreatEffect(String experimentId, String experimentPersonId, Integer periods,List<ExptTreatPlanItemVO> treatItems,Map<String, SpelEvalSumResult> mapSum ) {
        StandardEvaluationContext context = new SpelPersonContext().setVariables(experimentPersonId, periods);
        final Map<String, BigDecimal> mapTreatItem = ShareUtil.XCollection.toMap(treatItems, ExptTreatPlanItemVO::getTreatItemId, i ->
                BigDecimalUtil.tryParseDecimalElseNull(i.getWeight()));
        return spelEngine.loadFromSnapshot()
                .withReasonId(experimentId, experimentPersonId, mapTreatItem.keySet(), null)
                .prepare(inputs -> inputs.forEach(i -> i.setFactor(mapTreatItem.get(i.getReasonId()))))
                .evalSum(context, mapSum);
    }
    //endregion

    //region 突发事件
    //触发条件
    public boolean checkEventCondition(String experimentId, String experimentPersonId, String eventId, StandardEvaluationContext context) {
        return spelEngine.loadFromSnapshot()
                .withReasonId(experimentId, experimentPersonId, eventId,
                        EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource())
                .check(context);
    }
    public List<SpelCheckResult> checkEventCondition(String experimentId, String experimentPersonId,Integer period,Collection<String> eventIds) {
        return spelEngine.loadFromSnapshot()
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
        List<SpelEvalResult> evalResults= spelEngine.loadFromSnapshot()
                .withReasonId(experimentId, experimentPersonId, eventIds,
                        EnumIndicatorExpressionSource.EMERGENCY_INFLUENCE_INDICATOR.getSource())
                .evalSum(context,mapSum);
        return saveIndicator(evalResults,mapSum.values(),periods);

    }

    //处理措施作用
    public boolean saveEventAction(String experimentId, String experimentPersonId, Integer periods,Collection<String> actionIds){
        return saveEventAction(experimentId,experimentPersonId,periods,actionIds,
                new SpelPersonContext().setVariables(experimentPersonId,periods));
    }
    public boolean saveEventAction(String experimentId, String experimentPersonId, Integer periods,Collection<String> actionIds,StandardEvaluationContext context) {
        Map<String, SpelEvalSumResult> mapSum = new HashMap<>();
        List<SpelEvalResult> evalResults = spelEngine.loadFromSnapshot()
                .withReasonId(experimentId, experimentPersonId, actionIds,
                        EnumIndicatorExpressionSource.EMERGENCY_ACTION_INFLUENCE_INDICATOR.getSource())
                .evalSum(context, mapSum);
        return saveIndicator(evalResults,mapSum.values(), periods);
    }
    //endregion

    //region save

    public boolean saveIndicator(Collection<SpelEvalResult> evalResults, Collection<SpelEvalSumResult> evalSumResults,Integer periods){
        if(DEBUGFlag){
            saveIndicatorCurrent(evalSumResults,periods);
            saveIndicatorChange(evalSumResults);
            log.info("IndicatorDebug..."+JSONUtil.toJsonStr(evalResults));
            return true;
        }
        return saveIndicatorChange(evalSumResults);
    }


    public boolean saveIndicatorChange(Collection<SpelEvalSumResult> evalResults){
        if(ShareUtil.XObject.isEmpty(evalResults)) {
            return true;
        }
        List<ExperimentIndicatorInstanceRsEntity> rows=ShareUtil.XCollection.map(evalResults, false, i->
                new ExperimentIndicatorInstanceRsEntity().setExperimentIndicatorInstanceId(i.getExperimentIndicatorId())
                        .setChangeVal(i.getValdDouble()));

        return experimentIndicatorInstanceRsDao.updateIndicatorChange(rows);
    }


    public boolean saveIndicatorCurrent(Collection<SpelEvalSumResult> evalResults,Integer periods){
        if(ShareUtil.XObject.isEmpty(evalResults)) {
            return true;
        }
        List<ExperimentIndicatorValRsEntity> rows=ShareUtil.XCollection.map(evalResults, false, i->
                new ExperimentIndicatorValRsEntity().setIndicatorInstanceId(i.getExperimentIndicatorId())
                        .setPeriods(periods)
                        .setCurrentVal(i.getValString()));
        return experimentIndicatorValRsDao.updateIndicatorCurrent(rows);
    }


    //endregion





}
