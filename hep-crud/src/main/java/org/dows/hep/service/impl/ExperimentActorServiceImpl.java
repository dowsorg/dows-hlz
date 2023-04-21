package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentActorMapper;
import org.dows.hep.entity.ExperimentActorEntity;
import org.dows.hep.service.ExperimentActorService;
import org.springframework.stereotype.Service;


/**
 * 实验扮演者(ExperimentActor)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("experimentActorService")
public class ExperimentActorServiceImpl extends MybatisCrudServiceImpl<ExperimentActorMapper, ExperimentActorEntity> implements ExperimentActorService {

}

