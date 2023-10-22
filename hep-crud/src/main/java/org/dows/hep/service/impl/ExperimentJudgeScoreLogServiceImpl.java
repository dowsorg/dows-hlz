package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentJudgeScoreLogEntity;
import org.dows.hep.mapper.ExperimentJudgeScoreLogMapper;
import org.dows.hep.service.ExperimentJudgeScoreLogService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 15:23
 */
@Service("experimentJudgeScoreLogService")
public class ExperimentJudgeScoreLogServiceImpl extends MybatisCrudServiceImpl<ExperimentJudgeScoreLogMapper, ExperimentJudgeScoreLogEntity> implements ExperimentJudgeScoreLogService {
}
