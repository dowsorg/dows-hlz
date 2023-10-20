package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorJudgeGoalEntity;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:21
 */

@Mapper
public interface IndicatorJudgeGoalMapper extends MybatisCrudMapper<IndicatorJudgeGoalEntity> {
}
