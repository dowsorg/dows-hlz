package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.hep.mapper.ExperimentSysEventMapper;
import org.dows.hep.service.ExperimentSysEventService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/8/22 10:06
 */
@Service("experimentSysEventService")
public class ExperimentSysEventServiceImpl extends MybatisCrudServiceImpl<ExperimentSysEventMapper, ExperimentSysEventEntity> implements ExperimentSysEventService {
}
