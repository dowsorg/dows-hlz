package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentQuestionnaireItemEntity;
import org.dows.hep.mapper.ExperimentQuestionnaireItemMapper;
import org.dows.hep.service.ExperimentQuestionnaireItemService;
import org.springframework.stereotype.Service;

@Service("experimentQuestionnaireItemService")
public class ExperimentQuestionnaireItemServiceImpl extends MybatisCrudServiceImpl<ExperimentQuestionnaireItemMapper, ExperimentQuestionnaireItemEntity> implements ExperimentQuestionnaireItemService {
}
