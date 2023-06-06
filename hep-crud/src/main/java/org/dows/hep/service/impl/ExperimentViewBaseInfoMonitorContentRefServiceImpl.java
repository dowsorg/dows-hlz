package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoMonitorContentRefEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoMonitorContentRefMapper;
import org.dows.hep.service.ExperimentViewBaseInfoMonitorContentRefService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 15:36
 */
@Service("experimentViewBaseInfoMonitorContentRefService")
public class ExperimentViewBaseInfoMonitorContentRefServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoMonitorContentRefMapper, ExperimentViewBaseInfoMonitorContentRefEntity> implements ExperimentViewBaseInfoMonitorContentRefService {
}
