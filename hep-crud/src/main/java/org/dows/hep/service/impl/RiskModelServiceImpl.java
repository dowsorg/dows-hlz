package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.RiskModelMapper;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.RiskModelService;
import org.springframework.stereotype.Service;


/**
 * 风险模型(RiskModel)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:47
 */
@Service("riskModelService")
public class RiskModelServiceImpl extends MybatisCrudServiceImpl<RiskModelMapper, RiskModelEntity> implements RiskModelService {

}

