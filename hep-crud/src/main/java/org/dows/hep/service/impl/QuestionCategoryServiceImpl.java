package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionCategoryMapper;
import org.dows.hep.entity.QuestionCategoryEntity;
import org.dows.hep.service.QuestionCategoryService;
import org.springframework.stereotype.Service;


/**
 * 问题类目(QuestionCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:45
 */
@Service("questionCategoryService")
public class QuestionCategoryServiceImpl extends MybatisCrudServiceImpl<QuestionCategoryMapper, QuestionCategoryEntity> implements QuestionCategoryService {

}

