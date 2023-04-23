package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EventMapper;
import org.dows.hep.entity.EventEntity;
import org.dows.hep.service.EventService;
import org.springframework.stereotype.Service;


/**
 * 突发事件(Event)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("eventService")
public class EventServiceImpl extends MybatisCrudServiceImpl<EventMapper, EventEntity> implements EventService {

}

