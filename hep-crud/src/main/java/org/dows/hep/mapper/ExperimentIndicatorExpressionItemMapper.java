package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorExpressionEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemEntity;

/**
 * 实验指标公式细项(ExperimentIndicatorExpressionItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface ExperimentIndicatorExpressionItemMapper extends MybatisCrudMapper<ExperimentIndicatorExpressionItemEntity> {

}

