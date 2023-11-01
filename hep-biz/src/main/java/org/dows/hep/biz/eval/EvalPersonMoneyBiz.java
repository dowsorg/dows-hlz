package org.dows.hep.biz.eval;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.dao.OperateCostDao;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.OperateCostEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/9/23 20:11
 */

@RequiredArgsConstructor
@Component
public class EvalPersonMoneyBiz {

    private final OperateCostDao operateCostDao;
    private final IdGenerator idGenerator;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    private final ExperimentPersonCache experimentPersonCache;

    public Map<String, BigDecimalOptional> evalMoneyScore4Period(String experimentId,Integer periods) {
        Map<String, BigDecimalOptional> rst = new HashMap<>();
        Map<String, ExperimentPersonEntity> mapPersons = experimentPersonCache.getMapPersons(experimentId);
        if (mapPersons.isEmpty()) {
            return rst;
        }
        Map<String,BigDecimalOptional> mapInit=new HashMap<>();
        Map<String,BigDecimalOptional> mapCur=new HashMap<>();
        mapPersons.values().forEach(person->{
            String groupId=person.getExperimentGroupId();
            String personId=person.getExperimentPersonId();
            rst.putIfAbsent(groupId, BigDecimalOptional.zero());
            EvalIndicatorValues moneyValues=Optional.ofNullable(EvalPersonCache.Instance().getCurHolder(personId))
                    .map(i->i.getMoneyValues(periods))
                    .orElse(null);
            if(null==moneyValues){
                return;
            }
            mapInit.computeIfAbsent(groupId, k->BigDecimalOptional.create())
                    .add(BigDecimalUtil.tryParseDecimalElseZero( moneyValues.getPeriodInitVal()));
            mapCur.computeIfAbsent(groupId, k->BigDecimalOptional.create())
                    .add(BigDecimalUtil.tryParseDecimalElseZero( moneyValues.getCurVal()));

        });
        rst.forEach((k,v)->{
            v.setValue(getMoneyScore( Optional.ofNullable(mapCur.get(k)).map(i->i.getValue(SCALE)).orElse(BigDecimal.ZERO),
                    Optional.ofNullable(mapInit.get(k)).map(i->i.getValue(SCALE)).orElse(BigDecimal.ZERO)));
        });
        mapInit.clear();
        mapCur.clear();
        return rst;
    }
    private final int SCALE = 2;
    private BigDecimal getMoneyScore(BigDecimal money,BigDecimal lastMoney) {

        if (ShareUtil.XObject.anyEmpty(money, lastMoney)) {
            return BigDecimal.ZERO;
        }
        if (lastMoney.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if(money.compareTo(lastMoney)>=0){
            return BigDecimalUtil.ONEHundred;
        }
        return BigDecimalOptional.valueOf(money)
                .min(BigDecimal.ZERO)
                .mul(BigDecimalUtil.ONEHundred)
                .div(lastMoney, SCALE)
                .min(BigDecimal.ZERO)
                .max(BigDecimalUtil.ONEHundred)
                .getValue(SCALE);
    }

    @DSTransactional
    public boolean saveRefunds(String experimentId,Integer period, Set<String> experimentPersonIds){
        List<OperateCostEntity> refunds= getRefunds(experimentId,period,experimentPersonIds);
        if(ShareUtil.XObject.isEmpty(refunds)){
            return true;
        }
        refunds.forEach(i->experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest.builder()
                .appId("3")
                .experimentId(experimentId)
                .experimentPersonId(i.getPatientId())
                .periods(period)
                .moneyChange(i.getCost().negate())
                .build()));
        return operateCostDao.tranSaveBatch(refunds);
    }
    public List<OperateCostEntity> getRefunds(String experimentId,Integer period, Set<String> experimentPersonIds){
        Set<String> personIds=ExperimentPersonCache.Instance().getPersondIdSet(experimentId, experimentPersonIds);
        if(ShareUtil.XObject.isEmpty(personIds)){
            return Collections.emptyList();
        }
        List<OperateCostEntity> rowsCost=operateCostDao.getRefunds(experimentId, period, personIds,
                OperateCostEntity::getExperimentGroupId,
                OperateCostEntity::getExperimentOrgId,
                OperateCostEntity::getPeriod,
                OperateCostEntity::getPatientId,
                OperateCostEntity::getRestitution);
        if(ShareUtil.XObject.isEmpty(rowsCost)){
            return Collections.emptyList();
        }
        final String FEECodeBaoxiao="BXFH";
        Map<String,OperateCostEntity> mapRefund=new HashMap<>();
        rowsCost.forEach(i->{
            if(!FEECodeBaoxiao.equals(i.getFeeCode())){
                return;
            }
            mapRefund.put(i.getPatientId(), i);
        });
        Map<String,OperateCostEntity> mapCost=new HashMap<>();
        rowsCost.forEach(i->{
            if(mapRefund.containsKey(i.getPatientId())||FEECodeBaoxiao.equals(i.getFeeCode())) {
                return;
            }
            OperateCostEntity rowCost=mapCost.computeIfAbsent(i.getPatientId(), k->new OperateCostEntity()
                    .setOperateCostId(idGenerator.nextIdStr())
                    .setExperimentInstanceId(experimentId)
                    .setExperimentGroupId(i.getExperimentGroupId())
                    .setExperimentOrgId(i.getExperimentOrgId())
                    .setPeriod(period)
                    .setPatientId(i.getPatientId())
                    .setFeeCode(FEECodeBaoxiao)
                    .setFeeName("报销返还")
            );
            rowCost.setCost(BigDecimalUtil.add(rowCost.getCost(), Optional.ofNullable( i.getRestitution()).orElse(BigDecimal.ZERO).negate()));
        });
        List<OperateCostEntity> rst=mapCost.values().stream().toList();
        mapRefund.clear();
        mapCost.clear();
        rst.forEach(i->i.setRestitution(i.getCost()));
        return rst;

    }
}


