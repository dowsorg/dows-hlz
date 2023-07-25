package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.OperateInsuranceEntity;
import org.dows.hep.mapper.OperateInsuranceMapper;
import org.dows.hep.service.OperateInsuranceService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/7/25 15:40
 */
@Service("operateInsuranceService")
public class OperateInsuranceServiceImpl extends MybatisCrudServiceImpl<OperateInsuranceMapper, OperateInsuranceEntity> implements OperateInsuranceService {
}
