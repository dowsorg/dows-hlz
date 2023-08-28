package org.dows.hep.biz.event.sysevent.triggers;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/8/25 11:57
 */
@Component
public class SchemaStartTrigger implements ISysEventTrigger {
    @Override
    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        return null;
    }
}
