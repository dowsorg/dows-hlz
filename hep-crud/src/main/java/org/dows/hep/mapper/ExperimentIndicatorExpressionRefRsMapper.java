package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorExpressionRefRsEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;

/**
 * 指标公式关联(ExperimentIndicatorExpressionRefRsMapper)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface ExperimentIndicatorExpressionRefRsMapper extends MybatisCrudMapper<ExperimentIndicatorExpressionRefRsEntity> {

}

