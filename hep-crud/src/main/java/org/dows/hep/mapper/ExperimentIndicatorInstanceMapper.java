package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorInstanceEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;

/**
 * 实验指标(ExperimentIndicatorInstance)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface ExperimentIndicatorInstanceMapper extends MybatisCrudMapper<ExperimentIndicatorInstanceEntity> {

}

