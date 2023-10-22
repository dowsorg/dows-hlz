package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeGoalEntity;
import org.dows.hep.mapper.snapshot.SnapIndicatorJudgeGoalMapper;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeGoalService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 13:36
 */
@Service("snapIndicatorJudgeGoalService")
public class SnapIndicatorJudgeGoalServiceImpl extends MybatisCrudServiceImpl<SnapIndicatorJudgeGoalMapper, SnapIndicatorJudgeGoalEntity> implements SnapIndicatorJudgeGoalService {
}
