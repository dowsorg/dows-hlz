package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseIndicatorExpressionInfluenceEntity;
import org.dows.hep.entity.IndicatorExpressionInfluenceEntity;

/**
 * 指标公式影响(IndicatorExpressionInfluenceEntity)表数据库访问层
 * @author runsix
 */
@Mapper
public interface CaseIndicatorExpressionInfluenceMapper extends MybatisCrudMapper<CaseIndicatorExpressionInfluenceEntity> {

}

