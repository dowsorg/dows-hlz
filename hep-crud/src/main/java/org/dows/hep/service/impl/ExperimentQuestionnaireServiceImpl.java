package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.mapper.ExperimentQuestionnaireMapper;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.springframework.stereotype.Service;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 15:28
 */
@Service("experimentQuestionnaireService")
public class ExperimentQuestionnaireServiceImpl extends MybatisCrudServiceImpl<ExperimentQuestionnaireMapper, ExperimentQuestionnaireEntity> implements ExperimentQuestionnaireService {
}
