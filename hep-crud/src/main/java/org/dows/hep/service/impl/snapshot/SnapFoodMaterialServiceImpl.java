package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodMaterialEntity;
import org.dows.hep.mapper.snapshot.SnapFoodMaterialMapper;
import org.dows.hep.service.snapshot.SnapFoodMaterialService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodMaterialService")
public class SnapFoodMaterialServiceImpl extends MybatisCrudServiceImpl<SnapFoodMaterialMapper, SnapFoodMaterialEntity> implements SnapFoodMaterialService {

}
