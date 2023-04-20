package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentCaseReportEntity;

/**
 * 实验案例报告(ExperimentCaseReport)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:55:28
 */
@Mapper
public interface ExperimentCaseReportMapper extends MybatisCrudMapper<ExperimentCaseReportEntity> {

}

