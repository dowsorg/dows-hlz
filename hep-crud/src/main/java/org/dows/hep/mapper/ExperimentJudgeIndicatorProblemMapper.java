package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentJudgeIndicatorProblemEntity;

/**
 * @author jx
 * @date 2023/6/5 13:50
 */
@Mapper
public interface ExperimentJudgeIndicatorProblemMapper extends MybatisCrudMapper<ExperimentJudgeIndicatorProblemEntity> {
}
