package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;
import org.dows.hep.mapper.ExperimentFollowupPlanMapper;
import org.dows.hep.service.ExperimentFollowupPlanService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/2 19:10
 */
@Service("experimentFollowupPlanService")
public class ExperimentFollowupPlanServiceImpl extends MybatisCrudServiceImpl<ExperimentFollowupPlanMapper, ExperimentFollowupPlanEntity> implements ExperimentFollowupPlanService {
}
