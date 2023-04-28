package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.RankListItemEntity;

/**
 * 排行榜Item(RankListItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:53
 */
@Mapper
public interface RankListItemMapper extends MybatisCrudMapper<RankListItemEntity> {

}

