package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.QuestionSectionDimensionEntity;

/**
 * 问题集[试卷]-维度(QuestionSectionDimension)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:24
 */
@Mapper
public interface QuestionSectionDimensionMapper extends MybatisCrudMapper<QuestionSectionDimensionEntity> {

}

