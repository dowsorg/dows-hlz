package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author : wuzl
 * @date : 2023/8/23 14:27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaEndDealer extends BaseEventDealer {

    private final ExperimentTaskScheduleService experimentTaskScheduleService;
    private final ExperimentSchemeBiz experimentSchemeBiz;

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final String exptInstanceId=row.getEntity().getExperimentInstanceId();
        // 批量更新实验下所有方案设计的提交状态
        return experimentSchemeBiz.submitBatchWhenExpire(exptInstanceId);

    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSchemaMode()){
            return null;
        }
        return List.of(buildEvent(exptColl,0,
                EnumSysEventDealType.SCHEMAEnd.getCode(),
                EnumSysEventTriggerType.SCHEMAEnd.getCode()));
    }

}
