package org.dows.hep.biz.spel;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.ExperimentIndicatorValRsDao;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wuzl
 * @date : 2023/7/20 14:21
 */


@Slf4j
public class SpelPersonContext extends StandardEvaluationContext {

    public SpelPersonContext(){
        this(SpelVarKeyFormatter.PREFIX);
    }
    public SpelPersonContext(String varPrefix){
        this.varPrefix=varPrefix;
    }
    protected final String varPrefix;




    public SpelPersonContext setVariables(String experimentPersonId,Integer period) {
        loadIndicatorVals(experimentPersonId, period);
        return this;
    }


    private void loadIndicatorVals(String experimentPersonId,Integer period) {
        try {
            Collection<String> indicatorIds = PersonIndicatorIdCache.Instance().getIndicatorIds(experimentPersonId);
            List<ExperimentIndicatorValRsEntity> rowsVal = SpringUtil.getBean(ExperimentIndicatorValRsDao.class)
                    .getByExperimentIdAndIndicatorIds(null, indicatorIds, period,
                            ExperimentIndicatorValRsEntity::getIndicatorInstanceId,
                            ExperimentIndicatorValRsEntity::getCurrentVal);
            rowsVal.forEach(i->setVariable(SpelVarKeyFormatter.getVariableKey(i.getIndicatorInstanceId()), wrapVal(experimentPersonId,i.getIndicatorInstanceId(), i.getCurrentVal())));
            rowsVal.clear();
        } catch (Exception ex) {
            log.error(String.format("SpelPersonContext.loadIndicatorVals personId:%s period:%s", experimentPersonId, period), ex);
        }
    }


    private Object wrapVal(String experimentPersonId, String indicatorId, String str) {
        if (ShareUtil.XObject.isEmpty(str)) {
            str = Optional.ofNullable(PersonIndicatorIdCache.Instance().getIndicatorById(experimentPersonId, indicatorId))
                    .map(ExperimentIndicatorInstanceRsEntity::getDef)
                    .orElse(str);
        }
        return ShareUtil.XObject.isNumber(str) ? new BigDecimal(str) : str;
    }
}
