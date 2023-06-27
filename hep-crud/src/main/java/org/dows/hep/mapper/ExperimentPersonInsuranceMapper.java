package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonInsuranceEntity;

/**
 * @author jx
 * @date 2023/6/27 18:13
 */
@Mapper
public interface ExperimentPersonInsuranceMapper extends MybatisCrudMapper<ExperimentPersonInsuranceEntity> {
}
