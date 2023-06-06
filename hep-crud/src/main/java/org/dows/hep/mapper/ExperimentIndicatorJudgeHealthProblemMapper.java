package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemEntity;

/**
 * @author jx
 * @date 2023/6/5 14:13
 */
@Mapper
public interface ExperimentIndicatorJudgeHealthProblemMapper extends MybatisCrudMapper<ExperimentIndicatorJudgeHealthProblemEntity> {
}
