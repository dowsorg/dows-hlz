package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.GroupAverageHealthPointResponse;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.dows.hep.service.ExperimentScoringService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : wuzl
 * @date : 2023/9/8 12:48
 */
@Component
@RequiredArgsConstructor
public class QueryPersonBiz {
    private final EvalPersonCache evalPersonCache;

    private final PersonIndicatorIdCache personIndicatorIdCache;

    private final ExperimentPersonCache experimentPersonCache;

    private final ExperimentIndicatorValRsService experimentIndicatorValRsService;

    private final ExperimentScoringService experimentScoringService;

    public String getHealthPoint(Integer periods, String experimentPersonId){
        final String dft="1";
        ExperimentPersonEntity person=personIndicatorIdCache.getPerson(experimentPersonId);
        if(ShareUtil.XObject.isEmpty(person)){
            return dft;
        }

        return Optional.ofNullable( evalPersonCache.getCurHolder(person.getExperimentInstanceId(), experimentPersonId).getSysIndicator(EnumIndicatorType.HEALTH_POINT))
                .map(EvalIndicatorValues::getCurVal)
                .orElse(dft);
    }


    public boolean saveChangeMoney(ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity) {
        if (null == experimentIndicatorValRsEntity||null==experimentIndicatorValRsEntity.getIndicatorInstance()) {
            return false;
        }
        final ExperimentIndicatorInstanceRsEntity indicator=experimentIndicatorValRsEntity.getIndicatorInstance();
        evalPersonCache.getCurHolder(indicator.getExperimentId(),indicator.getExperimentPersonId())
                .putCurVal(indicator.getExperimentIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal(), true);
        return true;
    }

    public ExperimentIndicatorValRsEntity getChangeMoney(RsChangeMoneyRequest rsChangeMoneyRequest) {
        String appId = rsChangeMoneyRequest.getAppId();
        String experimentId = rsChangeMoneyRequest.getExperimentId();
        String experimentPersonId = rsChangeMoneyRequest.getExperimentPersonId();
        Integer periods = rsChangeMoneyRequest.getPeriods();
        BigDecimal moneyChange = rsChangeMoneyRequest.getMoneyChange();
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = personIndicatorIdCache.getSysIndicator(experimentPersonId,EnumIndicatorType.MONEY);
        String min = experimentIndicatorInstanceRsEntity.getMin();
        String max = experimentIndicatorInstanceRsEntity.getMax();
        String moneyVal = evalPersonCache.getCurHolder(experimentIndicatorInstanceRsEntity.getExperimentId(),experimentPersonId)
                .getIndicator(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())
                .getCurVal();
        BigDecimalOptional optMoney=BigDecimalOptional.valueOf(moneyVal).add(moneyChange);
        if (rsChangeMoneyRequest.isAssertEnough()) {
            AssertUtil.trueThenThrow(optMoney.getValue().compareTo(BigDecimal.ZERO) < 0)
                    .throwMessage("您的资金不足了");
        }
        moneyVal = optMoney.min(BigDecimalUtil.tryParseDecimalElseNull(min))
                .max(BigDecimalUtil.tryParseDecimalElseNull(max))
                .getString(2,RoundingMode.DOWN);
        return new ExperimentIndicatorValRsEntity().setIndicatorInstance(experimentIndicatorInstanceRsEntity)
                .setIndicatorInstanceId(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())
                .setCurrentVal(moneyVal);
    }

    public void populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
            Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
            String experimentPersonId,
            Integer curPeriods) {
        ExperimentPersonEntity person = personIndicatorIdCache.getPerson(experimentPersonId);
        if (ShareUtil.XObject.isEmpty(person)) {
            return;
        }
        evalPersonCache.getCurHolder(person.getExperimentInstanceId(), experimentPersonId)
                .fillCastMapCur(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);

    }


    public void fillIndicatorValMap( Map<String, String> kExperimentIndicatorInstanceIdVValMap, Integer periods, String experimentPersonId, Set<String> experimentIndicatorInstanceIdSet ){

        if(ConfigExperimentFlow.SWITCH2EvalCache){
            ExperimentPersonEntity person = personIndicatorIdCache.getPerson(experimentPersonId);
            if (ShareUtil.XObject.isEmpty(person)) {
                return;
            }
            evalPersonCache.getCurHolder(person.getExperimentInstanceId(), experimentPersonId)
                    .fillCastMapCur(kExperimentIndicatorInstanceIdVValMap,experimentIndicatorInstanceIdSet);
            return;
        }
        experimentIndicatorValRsService.lambdaQuery()
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
                .list()
                .forEach(experimentIndicatorValRsEntity -> {
                    kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
                });
    }

    public GroupAverageHealthPointResponse groupAverageHealth(String experimentId, String experimentGroupId, Integer periods) {
        List<ExperimentPersonEntity> persons=experimentPersonCache.getPersonsByGroupId(experimentId,experimentGroupId);
        if (ShareUtil.XObject.isEmpty(persons)) {
            return GroupAverageHealthPointResponse
                    .builder()
                    .experimentPersonCount(0)
                    .averageHealthPoint("0")
                    .build();
        }

        List<BigDecimal> valuedHp=new ArrayList<>();
        persons.forEach(person->{
            EvalPersonOnceHolder evalHolder=evalPersonCache.getCurHolder(person.getExperimentPersonId());
            BigDecimal curVal=Optional.ofNullable(evalHolder.getSysIndicator(EnumIndicatorType.HEALTH_POINT))
                    .map(i->BigDecimalUtil.tryParseDecimalElseZero(i.getCurVal()))
                    .orElse(null);
            if(ShareUtil.XObject.notEmpty(curVal)){
                valuedHp.add(curVal);
            }
        });

        final String avgHp=BigDecimalUtil.formatRoundDecimal(getAvg(valuedHp),2,RoundingMode.DOWN);
        int rank = 0;
        AtomicInteger curRank = new AtomicInteger(1);
        Map<String, Integer> kExperimentGroupIdVRankMap = new HashMap<>();
        experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
                .eq(ExperimentScoringEntity::getPeriods, periods - 1)
                .orderByDesc(ExperimentScoringEntity::getTotalScore)
                .list()
                .forEach(experimentScoringEntity -> {
                    kExperimentGroupIdVRankMap.put(experimentScoringEntity.getExperimentGroupId(), curRank.getAndIncrement());
                });
        if (Objects.nonNull(kExperimentGroupIdVRankMap.get(experimentGroupId))) {
            rank = kExperimentGroupIdVRankMap.get(experimentGroupId);
        }
        return GroupAverageHealthPointResponse
                .builder()
                .experimentPersonCount(persons.size())
                .averageHealthPoint(avgHp)
                .rank(rank)
                .build();
    }
    private BigDecimal getAvg(List<BigDecimal> values){
        if(ShareUtil.XObject.isEmpty(values)){
            return BigDecimal.ONE;
        }
        BigDecimalOptional total=BigDecimalOptional.create();
        values.forEach(i->total.add(i));
        return total.div(BigDecimalUtil.valueOf(values.size()), 2, RoundingMode.DOWN).getValue();

    }

    public Map<String, String> getSysIndicatorVals(Set<String> experimentPersonIdSet,EnumIndicatorType type,boolean initVal) {
        if(ShareUtil.XObject.isEmpty(experimentPersonIdSet)){
            return Collections.emptyMap();
        }
        Map<String, String> rst = new HashMap<>();
        experimentPersonIdSet.forEach(personId->{
            EvalPersonOnceHolder evalHolder=evalPersonCache.getCurHolder(personId);
            Optional.ofNullable(evalHolder.getSysIndicator(type))
                    .ifPresent(i->rst.put(personId, initVal?i.getPeriodInitVal():i.getCurVal()));
        });
        return rst;
    }
}
