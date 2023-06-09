package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewMonitorFollowupContentEntity;
import org.dows.hep.mapper.ExperimentViewMonitorFollowupContentMapper;
import org.dows.hep.service.ExperimentViewMonitorFollowupContentService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 19:31
 */
@Service("experimentViewMonitorFollowupContentService")
public class ExperimentViewMonitorFollowupContentServiceImpl extends MybatisCrudServiceImpl<ExperimentViewMonitorFollowupContentMapper, ExperimentViewMonitorFollowupContentEntity> implements ExperimentViewMonitorFollowupContentService {
}
