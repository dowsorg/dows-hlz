package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionSectionCategoryEntity;

/**
 * 问题集类目(QuestionSectionCategory)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:20
 */
@Mapper
public interface QuestionSectionCategoryMapper extends MybatisCrudMapper<QuestionSectionCategoryEntity> {

}

