package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoMonitorContentEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoMonitorContentMapper;
import org.dows.hep.service.ExperimentViewBaseInfoMonitorContentService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 15:20
 */
@Service("experimentViewBaseInfoMonitorContentService")
public class ExperimentViewBaseInfoMonitorContentServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoMonitorContentMapper, ExperimentViewBaseInfoMonitorContentEntity> implements ExperimentViewBaseInfoMonitorContentService {
}
