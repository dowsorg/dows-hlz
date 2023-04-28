package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateEventMapper;
import org.dows.hep.entity.OperateEventEntity;
import org.dows.hep.service.OperateEventService;
import org.springframework.stereotype.Service;


/**
 * 操作事件记录(OperateEvent)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("operateEventService")
public class OperateEventServiceImpl extends MybatisCrudServiceImpl<OperateEventMapper, OperateEventEntity> implements OperateEventService {

}

