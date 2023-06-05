package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeHealthProblemMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthProblemService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 14:12
 */
@Service("experimentIndicatorJudgeHealthProblemService")
public class ExperimentIndicatorJudgeHealthProblemServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeHealthProblemMapper, ExperimentIndicatorJudgeHealthProblemEntity> implements ExperimentIndicatorJudgeHealthProblemService {
}
