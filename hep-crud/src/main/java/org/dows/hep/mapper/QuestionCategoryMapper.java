package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionCategoryEntity;

import java.util.List;

/**
 * 问题类目(QuestionCategory)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:16
 */
@Mapper
public interface QuestionCategoryMapper extends MybatisCrudMapper<QuestionCategoryEntity> {

    List<QuestionCategoryEntity> getChildrenByPid(@Param("pid") String pid, @Param("categoryGroup") String categoryGroup);

    List<QuestionCategoryEntity> getAllCategory(@Param("categoryGroup") String categoryGroup);
}

