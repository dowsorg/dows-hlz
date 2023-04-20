package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentReportSchemaMapper;
import org.dows.hep.entity.ExperimentReportSchemaEntity;
import org.dows.hep.service.ExperimentReportSchemaService;
import org.springframework.stereotype.Service;


/**
 * 实验报告元数据(ExperimentReportSchema)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:56:08
 */
@Service("experimentReportSchemaService")
public class ExperimentReportSchemaServiceImpl extends MybatisCrudServiceImpl<ExperimentReportSchemaMapper, ExperimentReportSchemaEntity> implements ExperimentReportSchemaService {

}

