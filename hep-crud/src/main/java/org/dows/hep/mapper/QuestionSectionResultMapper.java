package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionSectionResultEntity;

/**
 * 问题集[试卷]-答题记录(QuestionSectionResult)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:52
 */
@Mapper
public interface QuestionSectionResultMapper extends MybatisCrudMapper<QuestionSectionResultEntity> {

}

