package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonHealthGuidanceEntity;

/**
 * @author jx
 * @date 2023/5/30 14:04
 */
@Mapper
public interface ExperimentPersonHealthGuidanceMapper extends MybatisCrudMapper<ExperimentPersonHealthGuidanceEntity> {
}
