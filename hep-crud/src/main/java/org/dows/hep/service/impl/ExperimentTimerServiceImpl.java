package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentTimerMapper;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;


/**
 * 实验计数计时器(ExperimentTimer)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("experimentTimerService")
public class ExperimentTimerServiceImpl extends MybatisCrudServiceImpl<ExperimentTimerMapper, ExperimentTimerEntity> implements ExperimentTimerService {

}

