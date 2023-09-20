package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.biz.dao.ExperimentFollowupPlanDao;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.user.experiment.ExperimentOrgNoticeBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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



    public boolean saveTriggeredFollowupPlan(ExperimentFollowupPlanEntity src, ExperimentTimePoint timePoint){
        if(ShareUtil.XObject.isEmpty(src)){
            return true;
        }
        ExperimentOrgNoticeEntity rowNotice=experimentOrgNoticeBiz.createNotice(src,timePoint);
        if(null==rowNotice){
            return true;
        }
        if(!experimentFollowupPlanDao.tranSave(src,false,()->experimentOrgNoticeBiz.add(rowNotice))) {
            return false;
        }
        experimentOrgNoticeBiz.pushNoticeSilence(src.getExperimentInstanceId(), EnumWebSocketType.FOLLOWUP_PLAN,List.of(rowNotice),false);
        return true;
    }
}
