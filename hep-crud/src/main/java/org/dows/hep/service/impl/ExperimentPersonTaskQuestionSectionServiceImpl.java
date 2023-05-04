package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPersonTaskQuestionSectionMapper;
import org.dows.hep.entity.ExperimentPersonTaskQuestionSectionEntity;
import org.dows.hep.service.ExperimentPersonTaskQuestionSectionService;
import org.springframework.stereotype.Service;


/**
 * 实验人物问题集(ExperimentPersonTaskQuestionSection)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:11
 */
@Service("experimentPersonTaskQuestionSectionService")
public class ExperimentPersonTaskQuestionSectionServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonTaskQuestionSectionMapper, ExperimentPersonTaskQuestionSectionEntity> implements ExperimentPersonTaskQuestionSectionService {

}

