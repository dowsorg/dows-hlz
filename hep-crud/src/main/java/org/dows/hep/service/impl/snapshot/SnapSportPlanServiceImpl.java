package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapSportPlanEntity;
import org.dows.hep.mapper.snapshot.SnapSportPlanMapper;
import org.dows.hep.service.snapshot.SnapSportPlanService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapSportPlanService")
public class SnapSportPlanServiceImpl extends MybatisCrudServiceImpl<SnapSportPlanMapper, SnapSportPlanEntity> implements SnapSportPlanService {

}
