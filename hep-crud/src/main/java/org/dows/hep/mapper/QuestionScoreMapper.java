package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionScoreEntity;

/**
 * 问题-得分(QuestionScore)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:15
 */
@Mapper
public interface QuestionScoreMapper extends MybatisCrudMapper<QuestionScoreEntity> {

}

