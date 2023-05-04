package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentGradeReportEntity;

/**
 * 实验成绩报告(ExperimentGradeReport)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:45
 */
@Mapper
public interface ExperimentGradeReportMapper extends MybatisCrudMapper<ExperimentGradeReportEntity> {

}

