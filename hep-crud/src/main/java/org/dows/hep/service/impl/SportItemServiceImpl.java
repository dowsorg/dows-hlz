package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.SportItemMapper;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.service.SportItemService;
import org.springframework.stereotype.Service;


/**
 * 运动项目(SportItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("sportItemService")
public class SportItemServiceImpl extends MybatisCrudServiceImpl<SportItemMapper, SportItemEntity> implements SportItemService {

}

