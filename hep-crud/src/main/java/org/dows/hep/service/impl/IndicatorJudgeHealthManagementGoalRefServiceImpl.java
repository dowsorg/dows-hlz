package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorJudgeHealthManagementGoalRefMapper;
import org.dows.hep.entity.IndicatorJudgeHealthManagementGoalRefEntity;
import org.dows.hep.service.IndicatorJudgeHealthManagementGoalRefService;
import org.springframework.stereotype.Service;


/**
 * 判断指标健管目标关联指标(IndicatorJudgeHealthManagementGoalRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:13
 */
@Service("indicatorJudgeHealthManagementGoalRefService")
public class IndicatorJudgeHealthManagementGoalRefServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeHealthManagementGoalRefMapper, IndicatorJudgeHealthManagementGoalRefEntity> implements IndicatorJudgeHealthManagementGoalRefService {

}

