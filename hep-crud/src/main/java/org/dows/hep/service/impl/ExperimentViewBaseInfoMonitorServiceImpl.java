package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoMonitorEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoMonitorMapper;
import org.dows.hep.service.ExperimentViewBaseInfoMonitorService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 11:36
 */
@Service("experimentViewBaseInfoMonitorService")
public class ExperimentViewBaseInfoMonitorServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoMonitorMapper, ExperimentViewBaseInfoMonitorEntity> implements ExperimentViewBaseInfoMonitorService {
}
