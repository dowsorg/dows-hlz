package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseOrgMapper;
import org.dows.hep.entity.CaseOrgEntity;
import org.dows.hep.service.CaseOrgService;
import org.springframework.stereotype.Service;


/**
 * 案例机构(CaseOrg)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:07
 */
@Service("caseOrgService")
public class CaseOrgServiceImpl extends MybatisCrudServiceImpl<CaseOrgMapper, CaseOrgEntity> implements CaseOrgService {

}

