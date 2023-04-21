package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseEventActionMapper;
import org.dows.hep.entity.CaseEventActionEntity;
import org.dows.hep.service.CaseEventActionService;
import org.springframework.stereotype.Service;


/**
 * 案例人物事件处理选项(CaseEventAction)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:40
 */
@Service("caseEventActionService")
public class CaseEventActionServiceImpl extends MybatisCrudServiceImpl<CaseEventActionMapper, CaseEventActionEntity> implements CaseEventActionService {

}

