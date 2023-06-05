package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeHealthGuidanceMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 14:21
 */
@Service("experimentIndicatorJudgeHealthGuidanceService")
public class ExperimentIndicatorJudgeHealthGuidanceServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeHealthGuidanceMapper, ExperimentIndicatorJudgeHealthGuidanceEntity> implements ExperimentIndicatorJudgeHealthGuidanceService {
}
