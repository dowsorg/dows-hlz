package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionAnswersEntity;

/**
 * 问题-答案(QuestionAnswers)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:00
 */
@Mapper
public interface QuestionAnswersMapper extends MybatisCrudMapper<QuestionAnswersEntity> {

}

