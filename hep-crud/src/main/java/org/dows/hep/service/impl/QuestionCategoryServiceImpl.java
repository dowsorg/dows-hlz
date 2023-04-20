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
 * @since 2023-04-18 13:59:02
 */
@Service("questionCategoryService")
public class QuestionCategoryServiceImpl extends MybatisCrudServiceImpl<QuestionCategoryMapper, QuestionCategoryEntity> implements QuestionCategoryService {

}

