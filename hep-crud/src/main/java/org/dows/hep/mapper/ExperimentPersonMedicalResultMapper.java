package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonMedicalResultEntity;

/**
 * @author jx
 * @date 2023/6/28 11:08
 */
@Mapper
public interface ExperimentPersonMedicalResultMapper extends MybatisCrudMapper<ExperimentPersonMedicalResultEntity> {
}
