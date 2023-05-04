package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.EvaluateDimensionExpressionEntity;

/**
 * 评估维度公式(EvaluateDimensionExpression)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:44
 */
@Mapper
public interface EvaluateDimensionExpressionMapper extends MybatisCrudMapper<EvaluateDimensionExpressionEntity> {

}

