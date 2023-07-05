package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapSportItemEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:46
 */
@Mapper
public interface SnapSportItemMapper  extends MybatisCrudMapper<SnapSportItemEntity> {

}
