package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemReportRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeHealthProblemReportRsMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthProblemReportRsService;
import org.springframework.stereotype.Service;


/**
 * 查看指标体格检查类(IndicatorJudgeHealthProblem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentIndicatorJudgeHealthProblemReportRsService")
public class ExperimentIndicatorJudgeHealthProblemReportRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeHealthProblemReportRsMapper, ExperimentIndicatorJudgeHealthProblemReportRsEntity> implements ExperimentIndicatorJudgeHealthProblemReportRsService {

}

