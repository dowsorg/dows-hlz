package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.event.EventScheduler;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author : wuzl
 * @date : 2023/8/23 14:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PeriodStartDealer extends BaseEventDealer {

    private final ExperimentTimerDao experimentTimerDao;


    @Override
    public boolean breakOnUnreached() {
        return false;
    }

    @Override
    public boolean breakOnFail() {
        return false;
    }

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final ExperimentSysEventEntity event = row.getEntity();
        final String appId = event.getAppId();
        final String experimentInstanceId = event.getExperimentInstanceId();
        //突发事件
        EventScheduler.Instance().scheduleTimeBasedEvent(appId, experimentInstanceId, 5);
        //实验流程
        EventScheduler.Instance().scheduleSysEvent(appId, experimentInstanceId, 3);

        /*this.pushTimeState(rst, ex, exptColl, EnumWebSocketType.FLOW_SAND_READY ,clientIds , row);*/
        return true;
    }




    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSandMode()){
            return null;
        }
        List<ExperimentSysEventEntity> rst=new ArrayList<>();
        for(int i=1;i<=exptColl.getPeriods();i++){
            rst.add(buildEvent(exptColl,i,
                    EnumSysEventDealType.PERIODStart,
                    EnumSysEventTriggerType.PERIODStart));
        }
        return rst;
    }

}
