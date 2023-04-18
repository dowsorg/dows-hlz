package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentReportSchemaEntity;

/**
 * 实验报告元数据(ExperimentReportSchema)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:56:09
 */
@Mapper
public interface ExperimentReportSchemaMapper extends MybatisCrudMapper<ExperimentReportSchemaEntity> {

}

