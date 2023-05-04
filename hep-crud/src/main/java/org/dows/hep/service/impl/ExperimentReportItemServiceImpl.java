package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentReportItemMapper;
import org.dows.hep.entity.ExperimentReportItemEntity;
import org.dows.hep.service.ExperimentReportItemService;
import org.springframework.stereotype.Service;


/**
 * 报告项(ExperimentReportItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("experimentReportItemService")
public class ExperimentReportItemServiceImpl extends MybatisCrudServiceImpl<ExperimentReportItemMapper, ExperimentReportItemEntity> implements ExperimentReportItemService {

}

