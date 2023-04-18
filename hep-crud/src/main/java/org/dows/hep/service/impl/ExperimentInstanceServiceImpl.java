package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentInstanceMapper;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.springframework.stereotype.Service;


/**
 * 实验实列(ExperimentInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:55:39
 */
@Service("experimentInstanceService")
public class ExperimentInstanceServiceImpl extends MybatisCrudServiceImpl<ExperimentInstanceMapper, ExperimentInstanceEntity> implements ExperimentInstanceService {

}

