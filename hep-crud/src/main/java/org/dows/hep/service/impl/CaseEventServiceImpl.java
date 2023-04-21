package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseEventMapper;
import org.dows.hep.entity.CaseEventEntity;
import org.dows.hep.service.CaseEventService;
import org.springframework.stereotype.Service;


/**
 * 案例人物事件(CaseEvent)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:40
 */
@Service("caseEventService")
public class CaseEventServiceImpl extends MybatisCrudServiceImpl<CaseEventMapper, CaseEventEntity> implements CaseEventService {

}

