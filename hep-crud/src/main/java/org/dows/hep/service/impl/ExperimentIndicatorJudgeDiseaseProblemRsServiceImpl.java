package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeDiseaseProblemEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeDiseaseProblemRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeDiseaseProblemMapper;
import org.dows.hep.mapper.ExperimentIndicatorJudgeDiseaseProblemRsMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeDiseaseProblemRsService;
import org.dows.hep.service.ExperimentIndicatorJudgeDiseaseProblemService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 14:30
 */
@Service("experimentIndicatorJudgeDiseaseProblemRsService")
public class ExperimentIndicatorJudgeDiseaseProblemRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeDiseaseProblemRsMapper, ExperimentIndicatorJudgeDiseaseProblemRsEntity> implements ExperimentIndicatorJudgeDiseaseProblemRsService {
}
