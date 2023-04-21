package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorJudgeHealthManagementGoalMapper;
import org.dows.hep.entity.IndicatorJudgeHealthManagementGoalEntity;
import org.dows.hep.service.IndicatorJudgeHealthManagementGoalService;
import org.springframework.stereotype.Service;


/**
 * 判断指标健管目标(IndicatorJudgeHealthManagementGoal)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorJudgeHealthManagementGoalService")
public class IndicatorJudgeHealthManagementGoalServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeHealthManagementGoalMapper, IndicatorJudgeHealthManagementGoalEntity> implements IndicatorJudgeHealthManagementGoalService {

}

