package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentCaseReportMapper;
import org.dows.hep.entity.ExperimentCaseReportEntity;
import org.dows.hep.service.ExperimentCaseReportService;
import org.springframework.stereotype.Service;


/**
 * 实验案例报告(ExperimentCaseReport)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("experimentCaseReportService")
public class ExperimentCaseReportServiceImpl extends MybatisCrudServiceImpl<ExperimentCaseReportMapper, ExperimentCaseReportEntity> implements ExperimentCaseReportService {

}

