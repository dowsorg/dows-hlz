package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentGradeReportMapper;
import org.dows.hep.entity.ExperimentGradeReportEntity;
import org.dows.hep.service.ExperimentGradeReportService;
import org.springframework.stereotype.Service;


/**
 * 实验成绩报告(ExperimentGradeReport)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:55:32
 */
@Service("experimentGradeReportService")
public class ExperimentGradeReportServiceImpl extends MybatisCrudServiceImpl<ExperimentGradeReportMapper, ExperimentGradeReportEntity> implements ExperimentGradeReportService {

}

