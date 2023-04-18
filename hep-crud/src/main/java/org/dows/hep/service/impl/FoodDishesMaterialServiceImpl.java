package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodDishesMaterialMapper;
import org.dows.hep.entity.FoodDishesMaterialEntity;
import org.dows.hep.service.FoodDishesMaterialService;
import org.springframework.stereotype.Service;


/**
 * 菜肴食材(FoodDishesMaterial)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:56:44
 */
@Service("foodDishesMaterialService")
public class FoodDishesMaterialServiceImpl extends MybatisCrudServiceImpl<FoodDishesMaterialMapper, FoodDishesMaterialEntity> implements FoodDishesMaterialService {

}

