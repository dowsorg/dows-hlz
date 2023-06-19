package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewPhysicalExamRsEntity;
import org.dows.hep.entity.IndicatorViewPhysicalExamEntity;

/**
 * 查看指标体格检查类(ExperimentIndicatorViewPhysicalExamRsMapper)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface ExperimentIndicatorViewPhysicalExamRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewPhysicalExamRsEntity> {

}

