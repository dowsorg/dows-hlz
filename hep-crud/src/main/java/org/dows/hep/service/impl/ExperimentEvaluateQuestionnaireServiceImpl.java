package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentEvaluateQuestionnaireEntity;
import org.dows.hep.mapper.ExperimentEvaluateQuestionnaireMapper;
import org.dows.hep.service.ExperimentEvaluateQuestionnaireService;
import org.springframework.stereotype.Service;

@Service("experimentEvaluateQuestionnaireService")
public class ExperimentEvaluateQuestionnaireServiceImpl extends MybatisCrudServiceImpl<ExperimentEvaluateQuestionnaireMapper, ExperimentEvaluateQuestionnaireEntity> implements ExperimentEvaluateQuestionnaireService {
}
