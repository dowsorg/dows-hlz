package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewMonitorFollowupEntity;
import org.dows.hep.mapper.ExperimentViewMonitorFollowupMapper;
import org.dows.hep.service.ExperimentViewMonitorFollowupService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 19:24
 */
@Service("experimentViewMonitorFollowupService")
public class ExperimentViewMonitorFollowupServiceImpl  extends MybatisCrudServiceImpl<ExperimentViewMonitorFollowupMapper, ExperimentViewMonitorFollowupEntity> implements ExperimentViewMonitorFollowupService {
}
