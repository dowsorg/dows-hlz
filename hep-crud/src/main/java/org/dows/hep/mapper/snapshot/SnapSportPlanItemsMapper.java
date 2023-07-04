package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapSportPlanItemsEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:49
 */
@Mapper
public interface SnapSportPlanItemsMapper  extends MybatisCrudMapper<SnapSportPlanItemsEntity> {

}
