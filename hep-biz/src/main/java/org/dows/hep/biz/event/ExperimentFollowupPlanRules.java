package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumEventActionState;
import org.dows.hep.api.enums.EnumExperimentOrgNoticeType;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.biz.dao.ExperimentFollowupPlanDao;
import org.dows.hep.biz.dao.ExperimentOrgNoticeDao;
import org.dows.hep.biz.eval.ExperimentPersonCache;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.user.experiment.ExperimentOrgNoticeBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/9/3 19:37
 */
@Component
@Slf4j
public class ExperimentFollowupPlanRules {
    private static volatile ExperimentFollowupPlanRules s_instance;
    public static ExperimentFollowupPlanRules Instance(){
        return s_instance;
    }

    private ExperimentFollowupPlanRules(){
        s_instance=this;
    }

    @Autowired
    private ExperimentOrgNoticeBiz experimentOrgNoticeBiz;

    @Autowired
    private ExperimentFollowupPlanDao experimentFollowupPlanDao;

    @Autowired
    private ExperimentOrgNoticeDao experimentOrgNoticeDao;



    public boolean saveTriggeredFollowupPlan(ExperimentFollowupPlanEntity src, ExperimentTimePoint timePoint) {
        if (ShareUtil.XObject.isEmpty(src)) {
            return true;
        }
        ExperimentPersonEntity rowPerson = ExperimentPersonCache.Instance().getPerson(src.getExperimentInstanceId(), src.getExperimentPersonId());
        if (ShareUtil.XObject.isEmpty(rowPerson)) {
            return false;
        }
        Set<String> monitorOrgIds = ExperimentPersonCache.Instance().getMonitorOrgIds(src.getExperimentInstanceId());
        final boolean pushNoticeFlag = monitorOrgIds.contains(rowPerson.getExperimentOrgId());
        ExperimentOrgNoticeEntity rowNotice = experimentOrgNoticeDao.getTopFollowupNotice(src.getExperimentPersonId(),
                ExperimentOrgNoticeEntity::getId,
                ExperimentOrgNoticeEntity::getExperimentPersonId,
                ExperimentOrgNoticeEntity::getAvatar,
                ExperimentOrgNoticeEntity::getTitle,
                ExperimentOrgNoticeEntity::getContent,
                ExperimentOrgNoticeEntity::getTips,
                ExperimentOrgNoticeEntity::getPersonName,
                ExperimentOrgNoticeEntity::getExperimentOrgNoticeId,
                ExperimentOrgNoticeEntity::getEventActions
        );

        if (null == rowNotice) {
            rowNotice = experimentOrgNoticeBiz.createNotice(src, rowPerson, timePoint);
        }
        rowNotice.setExperimentOrgId(rowPerson.getExperimentOrgId())
                .setExperimentGroupId(rowPerson.getExperimentGroupId())
                .setPeriods(timePoint.getPeriod())
                .setGameDay(timePoint.getGameDay())
                .setNoticeSrcType(EnumExperimentOrgNoticeType.FOLLOWUP.getCode())
                .setNoticeSrcId(src.getExperimentFollowupPlanId())
                .setEventActions(new StringBuilder(Optional.ofNullable(rowNotice.getEventActions()).orElse(""))
                        .append(" {day:").append(timePoint.getGameDay())
                        .append(" time:").append(timePoint.getRealTime())
                        .append(" org:").append(rowNotice.getExperimentOrgId())
                        .append(" push:").append(pushNoticeFlag)
                        .append(" },")
                        .toString());
       /* if(pushNoticeFlag){
            rowNotice.setNoticeTime(new Date())
                    .setReadState(0)
                    .setActionState(EnumEventActionState.TODO.getCode());
        }*/
        rowNotice.setNoticeTime(new Date())
                .setReadState(0)
                .setActionState(EnumEventActionState.TODO.getCode());
        final ExperimentOrgNoticeEntity saveNotice = rowNotice;
        if (!experimentFollowupPlanDao.tranSave(src, false, () -> experimentOrgNoticeBiz.upsert(saveNotice))) {
            return false;
        }
        if(pushNoticeFlag) {
            experimentOrgNoticeBiz.pushNoticeSilence(src.getExperimentInstanceId(), EnumWebSocketType.FOLLOWUP_PLAN, List.of(rowNotice), false);
        }
        return true;
    }
}
