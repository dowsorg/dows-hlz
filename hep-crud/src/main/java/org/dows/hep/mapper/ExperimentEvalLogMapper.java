package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentEvalLogEntity;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:10
 */
@Mapper
public interface ExperimentEvalLogMapper extends MybatisCrudMapper<ExperimentEvalLogEntity> {
}
