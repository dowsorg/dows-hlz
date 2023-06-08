package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewMonitorFollowupRecordEntity;
import org.dows.hep.mapper.ExperimentViewMonitorFollowupRecordMapper;
import org.dows.hep.service.ExperimentViewMonitorFollowupRecordService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/7 16:57
 */
@Service("experimentViewMonitorFollowupRecordService")
public class ExperimentViewMonitorFollowupRecordServiceImpl extends MybatisCrudServiceImpl<ExperimentViewMonitorFollowupRecordMapper, ExperimentViewMonitorFollowupRecordEntity> implements ExperimentViewMonitorFollowupRecordService {
}
