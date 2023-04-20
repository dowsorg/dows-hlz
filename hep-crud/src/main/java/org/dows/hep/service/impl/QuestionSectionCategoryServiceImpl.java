package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionSectionCategoryMapper;
import org.dows.hep.entity.QuestionSectionCategoryEntity;
import org.dows.hep.service.QuestionSectionCategoryService;
import org.springframework.stereotype.Service;


/**
 * 问题集类目(QuestionSectionCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:20
 */
@Service("questionSectionCategoryService")
public class QuestionSectionCategoryServiceImpl extends MybatisCrudServiceImpl<QuestionSectionCategoryMapper, QuestionSectionCategoryEntity> implements QuestionSectionCategoryService {

}

