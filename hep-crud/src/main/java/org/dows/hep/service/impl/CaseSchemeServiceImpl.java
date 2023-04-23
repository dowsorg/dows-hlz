package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseSchemeMapper;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.service.CaseSchemeService;
import org.springframework.stereotype.Service;


/**
 * 案例方案(CaseScheme)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("caseSchemeService")
public class CaseSchemeServiceImpl extends MybatisCrudServiceImpl<CaseSchemeMapper, CaseSchemeEntity> implements CaseSchemeService {

}

