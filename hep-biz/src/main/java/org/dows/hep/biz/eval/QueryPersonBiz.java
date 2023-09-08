package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
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
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    public String getHealthPoint(Integer periods, String experimentPersonId){
        final String dft="1";
        ExperimentPersonEntity person=personIndicatorIdCache.getPerson(experimentPersonId);
        if(ShareUtil.XObject.isEmpty(person)){
            return dft;
        }
        String indicatorId=personIndicatorIdCache.getSysIndicatorId(experimentPersonId, EnumIndicatorType.HEALTH_POINT);
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return dft;
        }
        return Optional.ofNullable( evalPersonCache.getCurHolder(person.getExperimentInstanceId(), experimentPersonId).getIndicator(indicatorId))
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
}
