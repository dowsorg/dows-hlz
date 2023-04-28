package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.MessageDefineMapper;
import org.dows.hep.entity.MessageDefineEntity;
import org.dows.hep.service.MessageDefineService;
import org.springframework.stereotype.Service;


/**
 * 消息定义(MessageDefine)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("messageDefineService")
public class MessageDefineServiceImpl extends MybatisCrudServiceImpl<MessageDefineMapper, MessageDefineEntity> implements MessageDefineService {

}

