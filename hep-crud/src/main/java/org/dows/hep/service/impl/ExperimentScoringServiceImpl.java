package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.mapper.ExperimentScoringMapper;
import org.dows.hep.service.ExperimentScoringService;
import org.springframework.stereotype.Service;


/**
 * 实验计分(ExperimentScoring)表服务实现类
 *
 * @author lait
 * @since 2023-07-04 11:31:39
 */
@Service("experimentScoringService")
public class ExperimentScoringServiceImpl extends MybatisCrudServiceImpl<ExperimentScoringMapper, ExperimentScoringEntity> implements ExperimentScoringService {

}

