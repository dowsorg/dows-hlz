package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodCookbookEntity;
import org.dows.hep.mapper.snapshot.SnapFoodCookbookMapper;
import org.dows.hep.service.snapshot.SnapFoodCookbookService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodCookbookService")
public class SnapFoodCookbookServiceImpl extends MybatisCrudServiceImpl<SnapFoodCookbookMapper, SnapFoodCookbookEntity> implements SnapFoodCookbookService {

}
