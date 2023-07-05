package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodMaterialNutrientEntity;
import org.dows.hep.mapper.snapshot.SnapFoodMaterialNutrientMapper;
import org.dows.hep.service.snapshot.SnapFoodMaterialNutrientService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodMaterialNutrientService")
public class SnapFoodMaterialNutrientServiceImpl extends MybatisCrudServiceImpl<SnapFoodMaterialNutrientMapper, SnapFoodMaterialNutrientEntity> implements SnapFoodMaterialNutrientService {

}
