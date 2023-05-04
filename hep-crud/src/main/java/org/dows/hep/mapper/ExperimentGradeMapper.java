package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentGradeEntity;

/**
 * 实验成绩(ExperimentGrade)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:45
 */
@Mapper
public interface ExperimentGradeMapper extends MybatisCrudMapper<ExperimentGradeEntity> {

}

