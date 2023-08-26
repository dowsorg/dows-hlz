package org.dows.hep.biz.event.sysevent;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;

/**
 * @author : wuzl
 * @date : 2023/8/25 11:49
 */
public interface ISysEventTrigger {

    ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl,Integer period,long cntPauseSeconds);
}
