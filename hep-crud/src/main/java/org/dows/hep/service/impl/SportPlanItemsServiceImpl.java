package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.SportPlanItemsMapper;
import org.dows.hep.entity.SportPlanItemsEntity;
import org.dows.hep.service.SportPlanItemsService;
import org.springframework.stereotype.Service;


/**
 * 运动方案项目列表(SportPlanItems)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("sportPlanItemsService")
public class SportPlanItemsServiceImpl extends MybatisCrudServiceImpl<SportPlanItemsMapper, SportPlanItemsEntity> implements SportPlanItemsService {

}

