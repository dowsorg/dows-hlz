package org.dows.hep.biz.event.sysevent.triggers;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/8/25 13:23
 */
@Component
public class PeriodStartTrigger implements ISysEventTrigger {
    @Override
    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        ExperimentTimePoint rst=new ExperimentTimePoint();
        if(!exptColl.hasSandMode()){
            return null;
        }
        if(ShareUtil.XObject.anyEmpty(exptColl.getSandStartTime(),exptColl.getPeriods())){
            return null;
        }
        ExperimentSettingCollection.ExperimentPeriodSetting periodSetting=exptColl.getSettingByPeriod(period);
        if(null==periodSetting){
            return null;
        }
        long triggerSeconds=periodSetting.getStartSecond()+cntPauseSeconds;
        return rst.setPeriod(period)
                .setCntPauseSeconds(cntPauseSeconds)
                .setGameDay(periodSetting.getStartGameDay())
                .setRealTime(exptColl.getSandStartTime().plusSeconds(triggerSeconds));
    }
}
