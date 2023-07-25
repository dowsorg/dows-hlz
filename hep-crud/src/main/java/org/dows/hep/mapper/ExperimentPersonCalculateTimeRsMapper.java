package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentCrowdsInstanceRsEntity;
import org.dows.hep.entity.ExperimentPersonCalculateTimeRsEntity;

/**
 * @author runsix
 * @date 2023/7/3 13:57
 */
@Mapper
public interface ExperimentPersonCalculateTimeRsMapper extends MybatisCrudMapper<ExperimentPersonCalculateTimeRsEntity> {
}
