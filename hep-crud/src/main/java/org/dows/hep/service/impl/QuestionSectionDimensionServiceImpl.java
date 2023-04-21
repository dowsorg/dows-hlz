package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionSectionDimensionMapper;
import org.dows.hep.entity.QuestionSectionDimensionEntity;
import org.dows.hep.service.QuestionSectionDimensionService;
import org.springframework.stereotype.Service;


/**
 * 问题集[试卷]-维度(QuestionSectionDimension)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:48
 */
@Service("questionSectionDimensionService")
public class QuestionSectionDimensionServiceImpl extends MybatisCrudServiceImpl<QuestionSectionDimensionMapper, QuestionSectionDimensionEntity> implements QuestionSectionDimensionService {

}

