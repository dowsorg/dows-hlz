package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceReportRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeHealthGuidanceReportRsMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthGuidanceReportRsService;
import org.springframework.stereotype.Service;


/**
 * 查看指标体格检查类(IndicatorJudgeHealthGuidance)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentIndicatorJudgeHealthGuidanceReportRsService")
public class ExperimentIndicatorJudgeHealthGuidanceReportRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeHealthGuidanceReportRsMapper, ExperimentIndicatorJudgeHealthGuidanceReportRsEntity> implements ExperimentIndicatorJudgeHealthGuidanceReportRsService {

}

