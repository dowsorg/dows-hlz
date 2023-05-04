package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.RankListItemMapper;
import org.dows.hep.entity.RankListItemEntity;
import org.dows.hep.service.RankListItemService;
import org.springframework.stereotype.Service;


/**
 * 排行榜Item(RankListItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("rankListItemService")
public class RankListItemServiceImpl extends MybatisCrudServiceImpl<RankListItemMapper, RankListItemEntity> implements RankListItemService {

}

