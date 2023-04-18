package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionCategoryEntity;

/**
 * 问题类目(QuestionCategory)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:03
 */
@Mapper
public interface QuestionCategoryMapper extends MybatisCrudMapper<QuestionCategoryEntity> {

}

