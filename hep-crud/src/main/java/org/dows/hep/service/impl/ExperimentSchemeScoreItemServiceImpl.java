package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentSchemeScoreItemEntity;
import org.dows.hep.mapper.ExperimentSchemeScoreItemMapper;
import org.dows.hep.service.ExperimentSchemeScoreItemService;
import org.springframework.stereotype.Service;

@Service("experimentSchemeScoreItemService")
public class ExperimentSchemeScoreItemServiceImpl extends MybatisCrudServiceImpl<ExperimentSchemeScoreItemMapper, ExperimentSchemeScoreItemEntity> implements ExperimentSchemeScoreItemService {
}
