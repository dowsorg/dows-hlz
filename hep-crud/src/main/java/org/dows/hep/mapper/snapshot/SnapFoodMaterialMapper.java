package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapFoodMaterialEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:16
 */
@Mapper
public interface SnapFoodMaterialMapper extends MybatisCrudMapper<SnapFoodMaterialEntity> {

}
