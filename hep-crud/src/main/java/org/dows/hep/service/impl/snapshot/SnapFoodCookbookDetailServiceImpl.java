package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodCookbookDetailEntity;
import org.dows.hep.mapper.snapshot.SnapFoodCookbookDetailMapper;
import org.dows.hep.service.snapshot.SnapFoodCookbookDetailService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodCookbookDetailService")
public class SnapFoodCookbookDetailServiceImpl extends MybatisCrudServiceImpl<SnapFoodCookbookDetailMapper, SnapFoodCookbookDetailEntity> implements SnapFoodCookbookDetailService {

}
