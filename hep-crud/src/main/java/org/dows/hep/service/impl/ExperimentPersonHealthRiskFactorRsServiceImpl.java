package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonHealthRiskFactorRsEntity;
import org.dows.hep.entity.ExperimentPersonRiskModelRsEntity;
import org.dows.hep.mapper.ExperimentPersonHealthRiskFactorRsMapper;
import org.dows.hep.mapper.ExperimentPersonRiskModelRsMapper;
import org.dows.hep.service.ExperimentPersonHealthRiskFactorRsService;
import org.dows.hep.service.ExperimentPersonRiskModelRsService;
import org.springframework.stereotype.Service;


/**
 * 风险模型(RiskModel)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("experimentPersonHealthRiskFactorRsService")
public class ExperimentPersonHealthRiskFactorRsServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonHealthRiskFactorRsMapper, ExperimentPersonHealthRiskFactorRsEntity> implements ExperimentPersonHealthRiskFactorRsService {

}

