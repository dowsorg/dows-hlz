package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodCookbookNutrientEntity;
import org.dows.hep.mapper.snapshot.SnapFoodCookbookNutrientMapper;
import org.dows.hep.service.snapshot.SnapFoodCookbookNutrientService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodCookbookNutrientService")
public class SnapFoodCookbookNutrientServiceImpl extends MybatisCrudServiceImpl<SnapFoodCookbookNutrientMapper, SnapFoodCookbookNutrientEntity> implements SnapFoodCookbookNutrientService {

}
