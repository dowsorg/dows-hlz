package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSchemeItemEntity;

/**
 * @author fhb
 * @description
 * @date 2023/6/6 21:06
 */

@Mapper
public interface ExperimentSchemeItemMapper extends MybatisCrudMapper<ExperimentSchemeItemEntity> {
}
