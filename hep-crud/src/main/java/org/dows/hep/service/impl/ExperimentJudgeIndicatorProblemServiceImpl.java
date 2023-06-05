package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentJudgeIndicatorProblemEntity;
import org.dows.hep.mapper.ExperimentJudgeIndicatorProblemMapper;
import org.dows.hep.service.ExperimentJudgeIndicatorProblemService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 13:49
 */
@Service("experimentJudgeIndicatorProblemService")
public class ExperimentJudgeIndicatorProblemServiceImpl extends MybatisCrudServiceImpl<ExperimentJudgeIndicatorProblemMapper, ExperimentJudgeIndicatorProblemEntity> implements ExperimentJudgeIndicatorProblemService {
}
