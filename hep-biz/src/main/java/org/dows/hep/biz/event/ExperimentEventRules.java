package org.dows.hep.biz.event;

import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentIndicatorValEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/22 22:17
 */
@Component
public class ExperimentEventRules {
    private static volatile ExperimentEventRules s_instance;
    public static ExperimentEventRules Instance(){
        return s_instance;
    }

    private ExperimentEventRules(){
        s_instance=this;
    }

    @Autowired
    private ExperimentEventDao experimentEventDao;

    //region facade
    public boolean saveTriggeredTimeEvent(List<ExperimentEventEntity> events){
        List<ExperimentIndicatorValEntity> indicatorVals=null;
        //TODO save indicator
        return experimentEventDao.tranSaveBatch(events,indicatorVals);
    }
    public boolean saveActionEvent(){
        return false;
    }
    //endregion

}
