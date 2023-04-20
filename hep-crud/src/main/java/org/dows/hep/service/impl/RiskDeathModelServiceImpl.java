package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.RiskDeathModelMapper;
import org.dows.hep.entity.RiskDeathModelEntity;
import org.dows.hep.service.RiskDeathModelService;
import org.springframework.stereotype.Service;


/**
 * 死亡模型(RiskDeathModel)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:44
 */
@Service("riskDeathModelService")
public class RiskDeathModelServiceImpl extends MybatisCrudServiceImpl<RiskDeathModelMapper, RiskDeathModelEntity> implements RiskDeathModelService {

}

