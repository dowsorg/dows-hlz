package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewSupportExamReportRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewSupportExamReportRsMapper;
import org.dows.hep.service.ExperimentIndicatorViewSupportExamReportRsService;
import org.springframework.stereotype.Service;


/**
 * 查看指标体格检查类(IndicatorViewSupportExam)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentIndicatorViewSupportExamReportRsService")
public class ExperimentIndicatorViewSupportExamReportRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewSupportExamReportRsMapper, ExperimentIndicatorViewSupportExamReportRsEntity> implements ExperimentIndicatorViewSupportExamReportRsService {

}

