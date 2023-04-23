package org.dows.hep.service;

import org.dows.framework.crud.mybatis.MybatisCrudService;
import org.dows.hep.entity.QuestionCategoryEntity;

import java.util.List;


/**
 * 问题类目(QuestionCategory)表服务接口
 *
 * @author lait
 * @since 2023-04-21 10:31:45
 */
public interface QuestionCategoryService extends MybatisCrudService<QuestionCategoryEntity> {

    List<QuestionCategoryEntity> getChildrenByPid(String pid, String categoryGroup);

    List<QuestionCategoryEntity> getAllCategory(String categoryGroup);
}

