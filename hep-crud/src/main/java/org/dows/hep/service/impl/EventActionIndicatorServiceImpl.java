package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EventActionIndicatorMapper;
import org.dows.hep.entity.EventActionIndicatorEntity;
import org.dows.hep.service.EventActionIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 事件处理选项影响指标(EventActionIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:54:34
 */
@Service("eventActionIndicatorService")
public class EventActionIndicatorServiceImpl extends MybatisCrudServiceImpl<EventActionIndicatorMapper, EventActionIndicatorEntity> implements EventActionIndicatorService {

}

