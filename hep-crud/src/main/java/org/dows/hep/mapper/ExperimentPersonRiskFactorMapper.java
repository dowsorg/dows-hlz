package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonRiskFactorEntity;

/**
 * @author jx
 * @date 2023/5/30 10:22
 */
@Mapper
public interface ExperimentPersonRiskFactorMapper extends MybatisCrudMapper<ExperimentPersonRiskFactorEntity> {
}
