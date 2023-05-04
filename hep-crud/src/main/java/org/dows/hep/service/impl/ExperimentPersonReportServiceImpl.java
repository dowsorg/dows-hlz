package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPersonReportMapper;
import org.dows.hep.entity.ExperimentPersonReportEntity;
import org.dows.hep.service.ExperimentPersonReportService;
import org.springframework.stereotype.Service;


/**
 * 实验人物报告(ExperimentPersonReport)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:11
 */
@Service("experimentPersonReportService")
public class ExperimentPersonReportServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonReportMapper, ExperimentPersonReportEntity> implements ExperimentPersonReportService {

}

