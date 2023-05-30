package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonRiskFactorEntity;
import org.dows.hep.mapper.ExperimentPersonRiskFactorMapper;
import org.dows.hep.service.ExperimentPersonRiskFactorService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/5/30 10:20
 */
@Service("experimentPersonRiskFactorService")
public class ExperimentPersonRiskFactorServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonRiskFactorMapper, ExperimentPersonRiskFactorEntity> implements ExperimentPersonRiskFactorService {
}
