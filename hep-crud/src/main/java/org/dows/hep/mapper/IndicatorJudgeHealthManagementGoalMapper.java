package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorJudgeHealthManagementGoalEntity;

/**
 * 判断指标健管目标(IndicatorJudgeHealthManagementGoal)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:26
 */
@Mapper
public interface IndicatorJudgeHealthManagementGoalMapper extends MybatisCrudMapper<IndicatorJudgeHealthManagementGoalEntity> {

}

