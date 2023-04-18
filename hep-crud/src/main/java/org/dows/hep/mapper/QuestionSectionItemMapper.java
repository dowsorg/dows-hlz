package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionSectionItemEntity;

/**
 * 问题集[试卷]-题目(QuestionSectionItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:27
 */
@Mapper
public interface QuestionSectionItemMapper extends MybatisCrudMapper<QuestionSectionItemEntity> {

}

