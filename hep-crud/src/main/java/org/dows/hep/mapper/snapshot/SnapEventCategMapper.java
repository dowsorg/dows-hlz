package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapEventCategEntity;

/**
 * @author : wuzl
 * @date : 2023/7/3 14:53
 */
@Mapper
public interface SnapEventCategMapper extends MybatisCrudMapper<SnapEventCategEntity> {

}
