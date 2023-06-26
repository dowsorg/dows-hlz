package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.mapper.ExperimentEventMapper;
import org.dows.hep.service.ExperimentEventService;
import org.springframework.stereotype.Service;


/**
 * 实验事件(ExperimentEvent)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentEventService")
public class ExperimentEventServiceImpl extends MybatisCrudServiceImpl<ExperimentEventMapper, ExperimentEventEntity> implements ExperimentEventService {

}

