package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionSectionEntity;

/**
 * 问题集[试卷](QuestionSection)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:16
 */
@Mapper
public interface QuestionSectionMapper extends MybatisCrudMapper<QuestionSectionEntity> {

}

