package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorLogEntity;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:08
 */
@Mapper
public interface ExperimentIndicatorLogMapper extends MybatisCrudMapper<ExperimentIndicatorLogEntity> {
}
