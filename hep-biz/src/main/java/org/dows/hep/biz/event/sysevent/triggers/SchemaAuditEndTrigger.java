package org.dows.hep.biz.event.sysevent.triggers;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @date 2023/9/5 11:54
 **/
@Component
public class SchemaAuditEndTrigger implements ISysEventTrigger {
    @Override
    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        ExperimentTimePoint rst = new ExperimentTimePoint();
        if(!exptColl.hasSchemaMode()) {
            return null;
        }

        return rst.setPeriod(0)
                .setGameDay(0)
                .setRealTime(exptColl.getSchemaAuditEndTime());
    }
}
