package org.dows.hep.biz.event.sysevent;

import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.data.EnumSysEventPushType;
import org.dows.hep.biz.event.sysevent.data.SysEventRow;
import org.dows.hep.biz.event.sysevent.data.SysEventRunStat;
import org.dows.hep.entity.ExperimentSysEventEntity;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/23 14:22
 */
public interface ISysEventDealer {


    /**
     * 重试次数
     * @return
     */
    default int maxRetryTimes(){
        return 5;
    }

    /**
     * 推送模式
     * @return
     */
    EnumSysEventPushType getPushType();

    /**
     * 未触发是否中断
     * @return
     */
    boolean breakOnUnreached();
    /**
     * 失败是否中断
     * @return
     */
    boolean breakOnFail();



    boolean dealEvent(SysEventRow row, SysEventRunStat stat);

    List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl);
}
