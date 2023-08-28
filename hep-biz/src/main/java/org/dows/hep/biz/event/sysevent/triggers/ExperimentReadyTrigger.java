package org.dows.hep.biz.event.sysevent.triggers;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/8/26 9:27
 */
@Component
public class ExperimentReadyTrigger implements ISysEventTrigger {

    @Override
    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        ExperimentTimePoint rst=new ExperimentTimePoint();
        return rst.setPeriod(0).setGameDay(0)
                .setRealTime(exptColl.getSandStartTime());
    }
}
