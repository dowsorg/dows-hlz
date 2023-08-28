package org.dows.hep.biz.event.sysevent.triggers;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/8/25 11:59
 */

@Component
public class ExperimentReportTrigger implements ISysEventTrigger {
    @Override
    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        ExperimentTimePoint rst=new ExperimentTimePoint();
        if(exptColl.hasSchemaMode()){
            return rst.setPeriod(0).setGameDay(0)
                    .setRealTime(exptColl.getSchemaEndTime());
        }
        if(ShareUtil.XObject.anyEmpty(exptColl.getSandStartTime(),exptColl.getPeriods())){
            return null;
        }
        period=exptColl.getPeriods();
        ExperimentSettingCollection.ExperimentPeriodSetting periodSetting=exptColl.getSettingByPeriod(period);
        if(null==periodSetting){
            return null;
        }
        long triggerSeconds=periodSetting.getEndSecond()+cntPauseSeconds;
        return rst.setPeriod(period)
                .setCntPauseSeconds(cntPauseSeconds)
                .setGameDay(periodSetting.getEndGameDay())
                .setRealTime(exptColl.getSandStartTime().plusSeconds(triggerSeconds));

    }
}
