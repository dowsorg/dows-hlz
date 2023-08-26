package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSysEventEntity;

/**
 * @author : wuzl
 * @date : 2023/8/22 10:00
 */
@Mapper
public interface ExperimentSysEventMapper extends MybatisCrudMapper<ExperimentSysEventEntity> {

}
