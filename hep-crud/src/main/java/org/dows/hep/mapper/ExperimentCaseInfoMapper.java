package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentCaseInfoEntity;

/**
 * @author fhb
 * @description
 * @date 2023/5/31 17:26
 */
@Mapper
public interface ExperimentCaseInfoMapper extends MybatisCrudMapper<ExperimentCaseInfoEntity> {
}
