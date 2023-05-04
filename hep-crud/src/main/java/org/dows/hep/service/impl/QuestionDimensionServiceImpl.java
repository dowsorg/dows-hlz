package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionDimensionMapper;
import org.dows.hep.entity.QuestionDimensionEntity;
import org.dows.hep.service.QuestionDimensionService;
import org.springframework.stereotype.Service;


/**
 * 问题-维度(QuestionDimension)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("questionDimensionService")
public class QuestionDimensionServiceImpl extends MybatisCrudServiceImpl<QuestionDimensionMapper, QuestionDimensionEntity> implements QuestionDimensionService {

}

