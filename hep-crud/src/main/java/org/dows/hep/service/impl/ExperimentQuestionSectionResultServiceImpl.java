package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentQuestionSectionResultMapper;
import org.dows.hep.entity.ExperimentQuestionSectionResultEntity;
import org.dows.hep.service.ExperimentQuestionSectionResultService;
import org.springframework.stereotype.Service;


/**
 * 实验问题集[试卷]-答题结果记录(ExperimentQuestionSectionResult)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("experimentQuestionSectionResultService")
public class ExperimentQuestionSectionResultServiceImpl extends MybatisCrudServiceImpl<ExperimentQuestionSectionResultMapper, ExperimentQuestionSectionResultEntity> implements ExperimentQuestionSectionResultService {

}

