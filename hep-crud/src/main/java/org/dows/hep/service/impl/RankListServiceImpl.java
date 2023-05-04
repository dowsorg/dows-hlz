package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.RankListMapper;
import org.dows.hep.entity.RankListEntity;
import org.dows.hep.service.RankListService;
import org.springframework.stereotype.Service;


/**
 * 排行榜(RankList)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("rankListService")
public class RankListServiceImpl extends MybatisCrudServiceImpl<RankListMapper, RankListEntity> implements RankListService {

}

