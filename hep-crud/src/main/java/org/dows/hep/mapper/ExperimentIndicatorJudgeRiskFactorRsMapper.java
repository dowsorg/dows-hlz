package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorRsEntity;
import org.dows.hep.entity.IndicatorJudgeRiskFactorEntity;

/**
 * 判断指标危险因素(IndicatorJudgeRiskFactor)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:49
 */
@Mapper
public interface ExperimentIndicatorJudgeRiskFactorRsMapper extends MybatisCrudMapper<ExperimentIndicatorJudgeRiskFactorRsEntity> {

}

