package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 * @author : wuzl
 * @date : 2023/8/23 15:40
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentReportDealer extends BaseEventDealer {

    private final ExperimentTimerBiz experimentTimerBiz;

    private final ExperimentTimerDao experimentTimerDao;

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final String appId=row.getEntity().getAppId();
        final String experimentInstanceId=row.getEntity().getExperimentInstanceId();

        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(ExperimentCacheKey.create(appId,experimentInstanceId),true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
            rst.append("missSetting[%s]",experimentInstanceId);
            return false;
        }

        List<ExperimentTimerEntity> rowsTimer=null;
        if(exptColl.hasSandMode()) {
            Map<Integer, ExperimentTimerEntity> mapTimers = experimentTimerDao.getMapByExperimentId(appId, experimentInstanceId, null,
                    ExperimentTimerEntity::getId,
                    ExperimentTimerEntity::getExperimentTimerId,
                    ExperimentTimerEntity::getPeriod,
                    ExperimentTimerEntity::getState);
            mapTimers.values().forEach(item -> {
                item.setState(EnumExperimentState.FINISH.getState());
            });
            rowsTimer=mapTimers.values().stream().toList();
            mapTimers.clear();
        }

        // 保存或更新实验计时器
        if(!experimentTimerBiz.saveOrUpdateExperimentTimeExperimentState(experimentInstanceId,rowsTimer, EnumExperimentState.FINISH)){
            rst.append("failUpdateExptState[%s]",experimentInstanceId);
            return false;
        }
        return true;

    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(exptColl.hasSandMode()) {
            return List.of(buildEvent(exptColl, exptColl.getPeriods(),
                    EnumSysEventDealType.EXPERIMENTReport,
                    EnumSysEventTriggerType.EXPERIMENTReport));
        }
        return null;
    }
}
