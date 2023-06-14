package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseOrgModuleFuncRefEntity;
import org.dows.hep.mapper.CaseModuleFuncRefMapper;
import org.dows.hep.service.CaseOrgModuleFuncRefService;
import org.springframework.stereotype.Service;


/**
 * 案例人物事件(CaseEvent)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:40
 */
@Service("caseOrgModuleEntityRefService")
public class CaseOrgOrgModuleFuncRefServiceImpl extends MybatisCrudServiceImpl<CaseModuleFuncRefMapper, CaseOrgModuleFuncRefEntity> implements CaseOrgModuleFuncRefService {

}

