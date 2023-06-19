package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseOrgModuleEntity;
import org.dows.hep.mapper.CaseModuleMapper;
import org.dows.hep.service.CaseOrgModuleService;
import org.springframework.stereotype.Service;


/**
 * 案例人物事件(CaseEvent)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:40
 */
@Service("caseOrgModuleService")
public class CaseOrgModuleServiceImpl extends MybatisCrudServiceImpl<CaseModuleMapper, CaseOrgModuleEntity> implements CaseOrgModuleService {

}

