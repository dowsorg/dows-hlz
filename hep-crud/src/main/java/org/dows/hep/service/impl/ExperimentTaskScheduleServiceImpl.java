package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.mapper.ExperimentTaskScheduleMapper;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.springframework.stereotype.Service;


/**
 * 实验任务调度(ExperimentTaskSchedule)表服务实现类
 *
 * @author lait
 * @since 2023-07-13 16:03:44
 */
@Service("experimentTaskScheduleService")
public class ExperimentTaskScheduleServiceImpl extends MybatisCrudServiceImpl<ExperimentTaskScheduleMapper, ExperimentTaskScheduleEntity> implements ExperimentTaskScheduleService {

}

