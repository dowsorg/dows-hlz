package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewMonitorFollowupContentRefEntity;
import org.dows.hep.mapper.ExperimentViewMonitorFollowupContentRefMapper;
import org.dows.hep.service.ExperimentViewMonitorFollowupContentRefService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 19:38
 */
@Service("experimentViewMonitorFollowupContentRefService")
public class ExperimentViewMonitorFollowupContentRefServiceImpl extends MybatisCrudServiceImpl<ExperimentViewMonitorFollowupContentRefMapper, ExperimentViewMonitorFollowupContentRefEntity> implements ExperimentViewMonitorFollowupContentRefService {
}
