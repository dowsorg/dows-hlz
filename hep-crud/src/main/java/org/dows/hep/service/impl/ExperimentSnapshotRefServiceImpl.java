package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentSnapshotRefEntity;
import org.dows.hep.mapper.ExperimentSnapshotRefMapper;
import org.dows.hep.service.ExperimentSnapshotRefService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/6/28 16:13
 */
@Service("experimentSnapshotRefService")
public class ExperimentSnapshotRefServiceImpl extends MybatisCrudServiceImpl<ExperimentSnapshotRefMapper, ExperimentSnapshotRefEntity> implements ExperimentSnapshotRefService {
}
