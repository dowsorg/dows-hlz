package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionOptionsEntity;

/**
 * 问题-选项(QuestionOptions)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:12
 */
@Mapper
public interface QuestionOptionsMapper extends MybatisCrudMapper<QuestionOptionsEntity> {

}

