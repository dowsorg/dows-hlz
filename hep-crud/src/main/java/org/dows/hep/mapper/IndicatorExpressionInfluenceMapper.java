package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorExpressionInfluenceEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;

/**
 * 指标公式影响(IndicatorExpressionInfluenceEntity)表数据库访问层
 * @author runsix
 */
@Mapper
public interface IndicatorExpressionInfluenceMapper extends MybatisCrudMapper<IndicatorExpressionInfluenceEntity> {

}

