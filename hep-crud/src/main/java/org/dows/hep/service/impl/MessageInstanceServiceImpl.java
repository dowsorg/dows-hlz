package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.MessageInstanceMapper;
import org.dows.hep.entity.MessageInstanceEntity;
import org.dows.hep.service.MessageInstanceService;
import org.springframework.stereotype.Service;


/**
 * 消息实例(MessageInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("messageInstanceService")
public class MessageInstanceServiceImpl extends MybatisCrudServiceImpl<MessageInstanceMapper, MessageInstanceEntity> implements MessageInstanceService {

}

