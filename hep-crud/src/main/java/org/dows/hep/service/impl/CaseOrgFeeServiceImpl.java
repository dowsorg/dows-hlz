package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseOrgFeeMapper;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.service.CaseOrgFeeService;
import org.springframework.stereotype.Service;


/**
 * 案例机构费用(CaseOrgFee)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("caseOrgFeeService")
public class CaseOrgFeeServiceImpl extends MybatisCrudServiceImpl<CaseOrgFeeMapper, CaseOrgFeeEntity> implements CaseOrgFeeService {

}

