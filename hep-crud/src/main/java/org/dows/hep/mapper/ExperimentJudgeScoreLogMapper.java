package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentJudgeScoreLogEntity;

/**
 * @author : wuzl
 * @date : 2023/10/21 15:21
 */
@Mapper
public interface ExperimentJudgeScoreLogMapper extends MybatisCrudMapper<ExperimentJudgeScoreLogEntity> {
}
