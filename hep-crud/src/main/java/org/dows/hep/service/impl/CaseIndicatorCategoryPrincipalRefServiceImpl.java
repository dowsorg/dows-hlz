package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorCategoryPrincipalRefEntity;
import org.dows.hep.entity.CaseIndicatorPrincipalRefEntity;
import org.dows.hep.mapper.CaseIndicatorCategoryPrincipalRefMapper;
import org.dows.hep.mapper.CaseIndicatorPrincipalRefMapper;
import org.dows.hep.service.CaseIndicatorCategoryPrincipalRefService;
import org.dows.hep.service.CaseIndicatorPrincipalRefService;
import org.springframework.stereotype.Service;


/**
 * 指标(CaseIndicatorCategoryPrincipalRefEntity)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorCategoryPrincipalRefService")
public class CaseIndicatorCategoryPrincipalRefServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorCategoryPrincipalRefMapper, CaseIndicatorCategoryPrincipalRefEntity> implements CaseIndicatorCategoryPrincipalRefService {

}

