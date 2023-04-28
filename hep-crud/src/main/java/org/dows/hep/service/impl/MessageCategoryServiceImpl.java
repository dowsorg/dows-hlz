package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.MessageCategoryMapper;
import org.dows.hep.entity.MessageCategoryEntity;
import org.dows.hep.service.MessageCategoryService;
import org.springframework.stereotype.Service;


/**
 * 消息类目(MessageCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("messageCategoryService")
public class MessageCategoryServiceImpl extends MybatisCrudServiceImpl<MessageCategoryMapper, MessageCategoryEntity> implements MessageCategoryService {

}

