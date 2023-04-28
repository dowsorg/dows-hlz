package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentParticipatorMapper;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Service;


/**
 * 实验组员（参与者）(ExperimentParticipator)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:10
 */
@Service("experimentParticipatorService")
public class ExperimentParticipatorServiceImpl extends MybatisCrudServiceImpl<ExperimentParticipatorMapper, ExperimentParticipatorEntity> implements ExperimentParticipatorService {

}

