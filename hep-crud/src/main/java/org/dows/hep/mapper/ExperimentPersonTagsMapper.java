package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonTagsEntity;

/**
 * @author jx
 * @date 2023/7/13 17:05
 */
@Mapper
public interface ExperimentPersonTagsMapper extends MybatisCrudMapper<ExperimentPersonTagsEntity> {
}
