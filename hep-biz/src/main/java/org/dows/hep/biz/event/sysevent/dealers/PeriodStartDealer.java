package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    @Override
    public boolean breakOnFail() {
        return false;
    }

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        return true;
    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSandMode()){
            return null;
        }
        List<ExperimentSysEventEntity> rst=new ArrayList<>();
        for(int i=1;i<exptColl.getPeriods();i++){
            rst.add(buildEvent(exptColl,i,
                    EnumSysEventDealType.PERIODStart.getCode(),
                    EnumSysEventTriggerType.PERIODStart.getCode()));
        }
        return rst;
    }

}
