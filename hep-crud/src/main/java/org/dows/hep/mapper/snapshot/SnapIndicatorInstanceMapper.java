package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapIndicatorInstanceEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:37
 */
@Mapper
public interface SnapIndicatorInstanceMapper extends MybatisCrudMapper<SnapIndicatorInstanceEntity> {

}
