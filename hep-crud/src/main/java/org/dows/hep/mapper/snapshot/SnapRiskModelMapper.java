package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;

/**
 * @author : wuzl
 * @date : 2023/9/14 9:57
 */
@Mapper
public interface SnapRiskModelMapper extends MybatisCrudMapper<SnapRiskModelEntity> {
}
