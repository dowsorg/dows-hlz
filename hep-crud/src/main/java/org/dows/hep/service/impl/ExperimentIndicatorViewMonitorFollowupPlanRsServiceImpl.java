package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentIndicatorViewMonitorFollowupPlanRsMapper;
import org.dows.hep.entity.ExperimentIndicatorViewMonitorFollowupPlanRsEntity;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupPlanRsService;
import org.springframework.stereotype.Service;


/**
 * (ExperimentIndicatorViewMonitorFollowupPlanRs)表服务实现类
 *
 * @author lait
 * @since 2023-07-24 14:55:16
 */
@Service("experimentIndicatorViewMonitorFollowupPlanRsService")
public class ExperimentIndicatorViewMonitorFollowupPlanRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewMonitorFollowupPlanRsMapper, ExperimentIndicatorViewMonitorFollowupPlanRsEntity> implements ExperimentIndicatorViewMonitorFollowupPlanRsService {

}

