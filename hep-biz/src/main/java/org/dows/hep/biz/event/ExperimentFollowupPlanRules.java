package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;
import org.springframework.stereotype.Component;

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

    public boolean saveTriggeredFollowupPlan(ExperimentFollowupPlanEntity src){
        return true;
    }
}
