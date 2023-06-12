package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorInstanceEntity;
import org.dows.hep.entity.CaseIndicatorPrincipalRefEntity;
import org.dows.hep.mapper.CaseIndicatorInstanceMapper;
import org.dows.hep.mapper.CaseIndicatorPrincipalRefMapper;
import org.dows.hep.service.CaseIndicatorInstanceService;
import org.dows.hep.service.CaseIndicatorPrincipalRefService;
import org.springframework.stereotype.Service;


/**
 * 指标(CaseIndicatorPrincipalRefEntity)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorPrincipalRefService")
public class CaseIndicatorPrincipalRefServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorPrincipalRefMapper, CaseIndicatorPrincipalRefEntity> implements CaseIndicatorPrincipalRefService {

}

