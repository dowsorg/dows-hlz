package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPeriodsQuestionMapper;
import org.dows.hep.entity.ExperimentPeriodsQuestionEntity;
import org.dows.hep.service.ExperimentPeriodsQuestionService;
import org.springframework.stereotype.Service;


/**
 * 实验期数答题(ExperimentPeriodsQuestion)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("experimentPeriodsQuestionService")
public class ExperimentPeriodsQuestionServiceImpl extends MybatisCrudServiceImpl<ExperimentPeriodsQuestionMapper, ExperimentPeriodsQuestionEntity> implements ExperimentPeriodsQuestionService {

}

