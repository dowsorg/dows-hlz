package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EventActionMapper;
import org.dows.hep.entity.EventActionEntity;
import org.dows.hep.service.EventActionService;
import org.springframework.stereotype.Service;


/**
 * 突发事件处理选项(EventAction)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("eventActionService")
public class EventActionServiceImpl extends MybatisCrudServiceImpl<EventActionMapper, EventActionEntity> implements EventActionService {

}

