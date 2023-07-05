package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapFoodDishesEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:26
 */
@Mapper
public interface SnapFoodDishesMapper extends MybatisCrudMapper<SnapFoodDishesEntity> {

}
