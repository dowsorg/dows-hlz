package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseOrgIndicatorMapper;
import org.dows.hep.entity.CaseOrgIndicatorEntity;
import org.dows.hep.service.CaseOrgIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 机构功能指标点(CaseOrgIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:54:30
 */
@Service("caseOrgIndicatorService")
public class CaseOrgIndicatorServiceImpl extends MybatisCrudServiceImpl<CaseOrgIndicatorMapper, CaseOrgIndicatorEntity> implements CaseOrgIndicatorService {

}

