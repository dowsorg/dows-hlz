package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonInsuranceEntity;
import org.dows.hep.mapper.ExperimentPersonInsuranceMapper;
import org.dows.hep.service.ExperimentPersonInsuranceService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/27 18:12
 */
@Service("experimentPersonInsuranceService")
public class ExperimentPersonInsuranceServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonInsuranceMapper, ExperimentPersonInsuranceEntity> implements ExperimentPersonInsuranceService {
}
