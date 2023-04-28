package org.dows.hep.service.impl;

import lombok.RequiredArgsConstructor;
import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.QuestionCategoryEntity;
import org.dows.hep.mapper.QuestionCategoryMapper;
import org.dows.hep.service.QuestionCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 问题类目(QuestionCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:45
 */
@RequiredArgsConstructor
@Service("questionCategoryService")
public class QuestionCategoryServiceImpl extends MybatisCrudServiceImpl<QuestionCategoryMapper, QuestionCategoryEntity> implements QuestionCategoryService {
    private final QuestionCategoryMapper questionCategoryMapper;

    @Override
    public List<QuestionCategoryEntity> getChildrenByPid(String pid, String categoryGroup) {
        return questionCategoryMapper.getChildrenByPid(pid, categoryGroup);
    }

    @Override
    public List<QuestionCategoryEntity> getAllCategory(String categoryGroup) {
        return questionCategoryMapper.getAllCategory(categoryGroup);
    }
}

