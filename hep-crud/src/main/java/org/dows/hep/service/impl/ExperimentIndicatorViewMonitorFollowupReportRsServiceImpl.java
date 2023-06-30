package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewMonitorFollowupReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorViewMonitorFollowupRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewMonitorFollowupReportRsMapper;
import org.dows.hep.mapper.ExperimentIndicatorViewMonitorFollowupRsMapper;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupReportRsService;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupRsService;
import org.springframework.stereotype.Service;


/**
 * 查看指标监测随访类(IndicatorViewMonitorFollowup)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("experimentIndicatorViewMonitorFollowupReportRsService")
public class ExperimentIndicatorViewMonitorFollowupReportRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewMonitorFollowupReportRsMapper, ExperimentIndicatorViewMonitorFollowupReportRsEntity> implements ExperimentIndicatorViewMonitorFollowupReportRsService {

}

