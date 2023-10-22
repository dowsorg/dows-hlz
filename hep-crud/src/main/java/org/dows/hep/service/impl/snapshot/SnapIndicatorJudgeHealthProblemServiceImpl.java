package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthProblemEntity;
import org.dows.hep.mapper.snapshot.SnapIndicatorJudgeHealthProblemMapper;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeHealthProblemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:32
 */
@Service("snapIndicatorJudgeHealthProblemService")
public class SnapIndicatorJudgeHealthProblemServiceImpl extends MybatisCrudServiceImpl<SnapIndicatorJudgeHealthProblemMapper, SnapIndicatorJudgeHealthProblemEntity> implements SnapIndicatorJudgeHealthProblemService {
}
