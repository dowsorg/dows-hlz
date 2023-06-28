package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorReportRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeRiskFactorReportRsMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeRiskFactorReportRsService;
import org.springframework.stereotype.Service;


/**
 * 查看指标体格检查类(IndicatorJudgeRiskFactor)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentIndicatorJudgeRiskFactorReportRsService")
public class ExperimentIndicatorJudgeRiskFactorReportRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeRiskFactorReportRsMapper, ExperimentIndicatorJudgeRiskFactorReportRsEntity> implements ExperimentIndicatorJudgeRiskFactorReportRsService {

}

