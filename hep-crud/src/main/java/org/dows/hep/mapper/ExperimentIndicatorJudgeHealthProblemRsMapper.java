package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemRsEntity;
import org.dows.hep.entity.IndicatorJudgeHealthProblemEntity;

/**
 * 判断指标健康问题(IndicatorJudgeHealthProblem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:49
 */
@Mapper
public interface ExperimentIndicatorJudgeHealthProblemRsMapper extends MybatisCrudMapper<ExperimentIndicatorJudgeHealthProblemRsEntity> {

}

