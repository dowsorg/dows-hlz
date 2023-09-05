package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.report.ExptReportFacadeBiz;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 方案设计审核结束
 * @date 2023/9/5 11:46
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaAuditEndDealer extends BaseEventDealer {
    private final ExptReportFacadeBiz exptReportFacadeBiz;

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final String exptInstanceId = row.getEntity().getExperimentInstanceId();
        // 方案设计评审时间截止后，重新生成报告
        return exptReportFacadeBiz.regenerate(exptInstanceId);
    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSchemaMode()){
            return null;
        }

        return List.of(buildEvent(exptColl,0,
                EnumSysEventDealType.SCHEMAAuditEnd,
                EnumSysEventTriggerType.SCHEMAAuditEnd));
    }
}
