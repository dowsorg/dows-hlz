package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentReportInstanceMapper;
import org.dows.hep.entity.ExperimentReportInstanceEntity;
import org.dows.hep.service.ExperimentReportInstanceService;
import org.springframework.stereotype.Service;


/**
 * 实验报告实例(ExperimentReportInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("experimentReportInstanceService")
public class ExperimentReportInstanceServiceImpl extends MybatisCrudServiceImpl<ExperimentReportInstanceMapper, ExperimentReportInstanceEntity> implements ExperimentReportInstanceService {

}

