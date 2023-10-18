package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.IndicatorJudgeGoalEntity;
import org.dows.hep.mapper.IndicatorJudgeGoalMapper;
import org.dows.hep.service.IndicatorJudgeGoalService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:25
 */

@Service("indicatorJudgeGoalService")
public class IndicatorJudgeGoalServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeGoalMapper, IndicatorJudgeGoalEntity> implements IndicatorJudgeGoalService {
}
