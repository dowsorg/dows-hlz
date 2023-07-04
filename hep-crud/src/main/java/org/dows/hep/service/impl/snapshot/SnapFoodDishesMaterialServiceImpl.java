package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodDishesMaterialEntity;
import org.dows.hep.mapper.snapshot.SnapFoodDishesMaterialMapper;
import org.dows.hep.service.snapshot.SnapFoodDishesMaterialService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodDishesMaterialService")
public class SnapFoodDishesMaterialServiceImpl extends MybatisCrudServiceImpl<SnapFoodDishesMaterialMapper, SnapFoodDishesMaterialEntity> implements SnapFoodDishesMaterialService {

}
