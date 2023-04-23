package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EventCategMapper;
import org.dows.hep.entity.EventCategEntity;
import org.dows.hep.service.EventCategService;
import org.springframework.stereotype.Service;


/**
 * 事件类别管理(EventCateg)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("eventCategService")
public class EventCategServiceImpl extends MybatisCrudServiceImpl<EventCategMapper, EventCategEntity> implements EventCategService {

}

