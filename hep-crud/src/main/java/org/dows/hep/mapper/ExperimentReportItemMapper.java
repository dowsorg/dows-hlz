package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentReportItemEntity;

/**
 * 报告项(ExperimentReportItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:56:04
 */
@Mapper
public interface ExperimentReportItemMapper extends MybatisCrudMapper<ExperimentReportItemEntity> {

}

