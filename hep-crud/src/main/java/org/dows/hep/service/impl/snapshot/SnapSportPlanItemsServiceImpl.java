package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapSportPlanItemsEntity;
import org.dows.hep.mapper.snapshot.SnapSportPlanItemsMapper;
import org.dows.hep.service.snapshot.SnapSportPlanItemsService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapSportPlanItemsService")
public class SnapSportPlanItemsServiceImpl extends MybatisCrudServiceImpl<SnapSportPlanItemsMapper, SnapSportPlanItemsEntity> implements SnapSportPlanItemsService {

}
