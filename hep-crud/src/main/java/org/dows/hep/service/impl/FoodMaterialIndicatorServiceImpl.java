package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodMaterialIndicatorMapper;
import org.dows.hep.entity.FoodMaterialIndicatorEntity;
import org.dows.hep.service.FoodMaterialIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 食材关联指标(FoodMaterialIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:13
 */
@Service("foodMaterialIndicatorService")
public class FoodMaterialIndicatorServiceImpl extends MybatisCrudServiceImpl<FoodMaterialIndicatorMapper, FoodMaterialIndicatorEntity> implements FoodMaterialIndicatorService {

}

