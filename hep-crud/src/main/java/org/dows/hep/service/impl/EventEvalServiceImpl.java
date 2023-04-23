package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EventEvalMapper;
import org.dows.hep.entity.EventEvalEntity;
import org.dows.hep.service.EventEvalService;
import org.springframework.stereotype.Service;


/**
 * 突发事件触发条件(EventEval)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("eventEvalService")
public class EventEvalServiceImpl extends MybatisCrudServiceImpl<EventEvalMapper, EventEvalEntity> implements EventEvalService {

}

