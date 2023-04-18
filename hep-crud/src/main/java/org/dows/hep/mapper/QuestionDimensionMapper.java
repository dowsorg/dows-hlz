package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionDimensionEntity;

/**
 * 问题-维度(QuestionDimension)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:06
 */
@Mapper
public interface QuestionDimensionMapper extends MybatisCrudMapper<QuestionDimensionEntity> {

}

