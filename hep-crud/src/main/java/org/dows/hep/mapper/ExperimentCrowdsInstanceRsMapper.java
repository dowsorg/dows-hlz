package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.entity.ExperimentCrowdsInstanceRsEntity;

/**
 * @author runsix
 * @date 2023/7/3 13:57
 */
@Mapper
public interface ExperimentCrowdsInstanceRsMapper extends MybatisCrudMapper<ExperimentCrowdsInstanceRsEntity> {
}
