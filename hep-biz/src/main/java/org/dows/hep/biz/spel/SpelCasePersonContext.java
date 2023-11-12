package org.dows.hep.biz.spel;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.dao.CaseIndicatorInstanceDao;
import org.dows.hep.biz.dao.CaseIndicatorRuleDao;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorInstanceEntity;
import org.dows.hep.entity.CaseIndicatorRuleEntity;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/10/16 14:21
 */


@Slf4j
public class SpelCasePersonContext extends StandardEvaluationContext {

    public SpelCasePersonContext(){
        this(SpelVarKeyFormatter.PREFIX);
    }
    public SpelCasePersonContext(String varPrefix){
        this.varPrefix=varPrefix;
    }
    protected final String varPrefix;

    protected String indicatorId4HealthIndex;



    public String getIndicatorId4HealthIndex() {
        return indicatorId4HealthIndex;
    }


    public SpelCasePersonContext setVariables(String accountId,boolean useBaseIndicatorId) {
        loadIndicatorVals(accountId, useBaseIndicatorId);
        return this;
    }

    public SpelCasePersonContext setCurVal(String indicatorId, String curVal){
        this.setVariable(SpelVarKeyFormatter.getVariableKey(indicatorId,false), wrapVal(curVal));
        return this;
    }
    public SpelCasePersonContext setLastVal(String indicatorId, String lastVal){
        this.setVariable(SpelVarKeyFormatter.getVariableKey(indicatorId,true), wrapVal(lastVal));
        return this;
    }

    @Override
    public void setVariable(String name, Object value) {
        if(null==name||null==value){
            return;
        }
        try {
            super.setVariable(name, value);
        }catch (Exception ex){
            log.error(String.format("SPELTrace--SpelCasePersonContext.setVariable name:%s value:%s", name, value), ex);
        }

    }

    private void loadIndicatorVals(String accountId,boolean useBaseIndicatorId) {
        try {
            final Map<String, String> mapCase2BaseId = new HashMap<>();
            final Set<String> caseIds = new HashSet<>();
            SpringUtil.getBean(CaseIndicatorInstanceDao.class).getByPersonId(null, accountId,
                            CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId,
                            CaseIndicatorInstanceEntity::getIndicatorInstanceId,
                            CaseIndicatorInstanceEntity::getType)
                    .forEach(i -> {
                        if(i.getType().equals(EnumIndicatorType.HEALTH_POINT.getType())){
                            indicatorId4HealthIndex=i.getCaseIndicatorInstanceId();
                        }
                        caseIds.add(i.getCaseIndicatorInstanceId());
                        if (ShareUtil.XObject.isEmpty(i.getIndicatorInstanceId())) {
                            return;
                        }
                        mapCase2BaseId.put(i.getCaseIndicatorInstanceId(), i.getIndicatorInstanceId());
                    });
            SpringUtil.getBean(CaseIndicatorRuleDao.class).getByIndicatorIds(caseIds,
                            CaseIndicatorRuleEntity::getVariableId,
                            CaseIndicatorRuleEntity::getDef)
                    .forEach(i -> {
                        String indicatorId = useBaseIndicatorId ? mapCase2BaseId.get(i.getVariableId()) : i.getVariableId();
                        if (ShareUtil.XObject.isEmpty(indicatorId)) {
                            return;
                        }
                        setCurVal(indicatorId, i.getDef())
                                .setLastVal(indicatorId, i.getDef());
                    });

        } catch (Exception ex) {
            log.error(String.format("SpelCasePersonContext.loadIndicatorVals accountId:%s", accountId), ex);
        }
    }


    private Object wrapVal( String str) {
        str=null==str?"":str.trim();
        return ShareUtil.XObject.isNumber(str) ? new BigDecimal(str).setScale(2, RoundingMode.HALF_UP)  : str;
    }
}
