package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.mapper.snapshot.SnapIndicatorJudgeHealthGuidanceMapper;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:32
 */

@Service("snapIndicatorJudgeHealthGuidanceService")
public class SnapIndicatorJudgeHealthGuidanceServiceImpl extends MybatisCrudServiceImpl<SnapIndicatorJudgeHealthGuidanceMapper, SnapIndicatorJudgeHealthGuidanceEntity> implements SnapIndicatorJudgeHealthGuidanceService {
}
