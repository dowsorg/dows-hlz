package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSnapshotRefEntity;

/**
 * @author : wuzl
 * @date : 2023/6/28 16:10
 */
@Mapper
public interface ExperimentSnapshotRefMapper extends MybatisCrudMapper<ExperimentSnapshotRefEntity> {
}
