package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.RiskDangerPointMapper;
import org.dows.hep.entity.RiskDangerPointEntity;
import org.dows.hep.service.RiskDangerPointService;
import org.springframework.stereotype.Service;


/**
 * 危险分数(RiskDangerPoint)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:41
 */
@Service("riskDangerPointService")
public class RiskDangerPointServiceImpl extends MybatisCrudServiceImpl<RiskDangerPointMapper, RiskDangerPointEntity> implements RiskDangerPointService {

}

