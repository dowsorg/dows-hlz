package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentSchemeScoreEntity;
import org.dows.hep.mapper.ExperimentSchemeScoreMapper;
import org.dows.hep.service.ExperimentSchemeScoreService;
import org.springframework.stereotype.Service;

@Service("experimentSchemeScoreService")
public class ExperimentSchemeScoreServiceImpl extends MybatisCrudServiceImpl<ExperimentSchemeScoreMapper, ExperimentSchemeScoreEntity> implements ExperimentSchemeScoreService {
}
