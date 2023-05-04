package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.SportPlanMapper;
import org.dows.hep.entity.SportPlanEntity;
import org.dows.hep.service.SportPlanService;
import org.springframework.stereotype.Service;


/**
 * 运动方案(SportPlan)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("sportPlanService")
public class SportPlanServiceImpl extends MybatisCrudServiceImpl<SportPlanMapper, SportPlanEntity> implements SportPlanService {

}

