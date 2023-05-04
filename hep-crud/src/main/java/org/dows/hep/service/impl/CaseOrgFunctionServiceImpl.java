package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseOrgFunctionMapper;
import org.dows.hep.entity.CaseOrgFunctionEntity;
import org.dows.hep.service.CaseOrgFunctionService;
import org.springframework.stereotype.Service;


/**
 * 机构功能(CaseOrgFunction)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("caseOrgFunctionService")
public class CaseOrgFunctionServiceImpl extends MybatisCrudServiceImpl<CaseOrgFunctionMapper, CaseOrgFunctionEntity> implements CaseOrgFunctionService {

}

