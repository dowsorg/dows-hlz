package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonRiskModelRsEntity;
import org.dows.hep.entity.ExperimentRiskModelRsEntity;
import org.dows.hep.mapper.ExperimentPersonRiskModelRsMapper;
import org.dows.hep.mapper.ExperimentRiskModelRsMapper;
import org.dows.hep.service.ExperimentPersonRiskModelRsService;
import org.dows.hep.service.ExperimentRiskModelRsService;
import org.springframework.stereotype.Service;


/**
 * 风险模型(RiskModel)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("experimentPersonRiskModelRsService")
public class ExperimentPersonRiskModelRsServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonRiskModelRsMapper, ExperimentPersonRiskModelRsEntity> implements ExperimentPersonRiskModelRsService {

}

