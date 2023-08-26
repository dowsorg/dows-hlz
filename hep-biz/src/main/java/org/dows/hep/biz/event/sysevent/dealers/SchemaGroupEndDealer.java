package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.ExperimentGroupDao;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/26 7:36
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaGroupEndDealer extends BaseEventDealer {

    private final ExperimentSchemeBiz experimentSchemeBiz;

    private final ExperimentGroupDao experimentGroupDao;

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final String exptId=row.getEntity().getExperimentInstanceId();
        final String exptGroupId=row.getEntity().getExperimentGroupId();
        return experimentSchemeBiz.submitWhen0RemainingTime(exptId, exptGroupId);
    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(!exptColl.hasSchemaMode()){
            return null;
        }
        final String exptId=exptColl.getExperimentInstanceId();
        List<ExperimentGroupEntity> rowsGroup=experimentGroupDao.getByExperimentId(exptId,
                ExperimentGroupEntity::getExperimentGroupId);
        List<ExperimentSysEventEntity> rst=new ArrayList<>();
        rowsGroup.forEach(item-> rst.add(buildEvent(exptColl,0,
                EnumSysEventDealType.SCHEMAEnd.getCode(),
                EnumSysEventTriggerType.MANUAL.getCode())
                .setExperimentGroupId(item.getExperimentGroupId())));
        return rst;
    }
}
