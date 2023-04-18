package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodMaterialMapper;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.service.FoodMaterialService;
import org.springframework.stereotype.Service;


/**
 * 食材(FoodMaterial)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:56:51
 */
@Service("foodMaterialService")
public class FoodMaterialServiceImpl extends MybatisCrudServiceImpl<FoodMaterialMapper, FoodMaterialEntity> implements FoodMaterialService {

}

