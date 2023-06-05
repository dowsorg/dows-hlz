package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeRiskFactorMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeRiskFactorService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 13:49
 */
@Service("experimentIndicatorJudgeRiskFactorService")
public class ExperimentIndicatorJudgeRiskFactorServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeRiskFactorMapper, ExperimentIndicatorJudgeRiskFactorEntity> implements ExperimentIndicatorJudgeRiskFactorService {
}
