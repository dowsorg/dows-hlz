package org.dows.hep.biz.event.sysevent.dealers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.eval.EvalScoreRankBiz;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.BaseEventDealer;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.report.ExptReportFacadeBiz;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    private final ExperimentScoringBiz experimentScoringBiz;

    private final EvalScoreRankBiz evalScoreRankBiz;

    private final ExptReportFacadeBiz exptReportFacadeBiz;

    @Override
    protected boolean coreDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat) {
        final String appId=row.getEntity().getAppId();
        final String experimentInstanceId=row.getEntity().getExperimentInstanceId();

        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(ExperimentCacheKey.create(appId,experimentInstanceId),true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
            rst.append("missSetting[%s]",experimentInstanceId);
            return false;
        }

        final String accountId="admin";
        final boolean regenerate=false;
        exptReportFacadeBiz.exportGroupReport(experimentInstanceId, null, accountId, regenerate, regenerate);
        exptReportFacadeBiz.exportExptReport(experimentInstanceId,accountId , true, regenerate, regenerate);
        return true;

    }

    @Override
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        if(exptColl.hasSandMode()) {
            return List.of(buildEvent(exptColl, exptColl.getPeriods(),
                    EnumSysEventDealType.EXPERIMENTReport,
                    EnumSysEventTriggerType.SANDEnd));
        }
        return null;
    }
}
