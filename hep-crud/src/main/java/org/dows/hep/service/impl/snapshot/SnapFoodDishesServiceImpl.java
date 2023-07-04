package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodDishesEntity;
import org.dows.hep.mapper.snapshot.SnapFoodDishesMapper;
import org.dows.hep.service.snapshot.SnapFoodDishesService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodDishesService")
public class SnapFoodDishesServiceImpl extends MybatisCrudServiceImpl<SnapFoodDishesMapper, SnapFoodDishesEntity> implements SnapFoodDishesService {

}
