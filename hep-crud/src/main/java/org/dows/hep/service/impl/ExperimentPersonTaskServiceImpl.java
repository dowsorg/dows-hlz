package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPersonTaskMapper;
import org.dows.hep.entity.ExperimentPersonTaskEntity;
import org.dows.hep.service.ExperimentPersonTaskService;
import org.springframework.stereotype.Service;


/**
 * 实验人物任务(ExperimentPersonTask)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:11
 */
@Service("experimentPersonTaskService")
public class ExperimentPersonTaskServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonTaskMapper, ExperimentPersonTaskEntity> implements ExperimentPersonTaskService {

}

