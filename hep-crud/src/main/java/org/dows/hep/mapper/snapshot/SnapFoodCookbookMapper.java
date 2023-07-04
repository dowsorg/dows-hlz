package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapFoodCookbookEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:28
 */
@Mapper
public interface SnapFoodCookbookMapper extends MybatisCrudMapper<SnapFoodCookbookEntity> {

}
