package org.dows.hep.biz.event.sysevent.triggers;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/8/25 11:58
 */
@Component
public class SandStartTrigger implements ISysEventTrigger {
    @Override
    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        ExperimentTimePoint rst=new ExperimentTimePoint();
        if(!exptColl.hasSandMode()){
            return null;
        }
        if(ShareUtil.XObject.anyEmpty(exptColl.getSandStartTime(),exptColl.getPeriods())){
            return null;
        }
        return rst.setPeriod(0)
                .setGameDay(0)
                .setRealTime(exptColl.getSandStartTime());
    }
}
