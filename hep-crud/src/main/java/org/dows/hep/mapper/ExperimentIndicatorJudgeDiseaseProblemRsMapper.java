package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorJudgeDiseaseProblemRsEntity;
import org.dows.hep.entity.IndicatorJudgeDiseaseProblemEntity;

/**
 * 判断指标疾病问题(IndicatorJudgeDiseaseProblem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:49
 */
@Mapper
public interface ExperimentIndicatorJudgeDiseaseProblemRsMapper extends MybatisCrudMapper<ExperimentIndicatorJudgeDiseaseProblemRsEntity> {

}

