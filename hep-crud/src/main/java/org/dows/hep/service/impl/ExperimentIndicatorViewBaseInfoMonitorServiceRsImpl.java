package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewBaseInfoMonitorRsEntity;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewBaseInfoMonitorRsMapper;
import org.dows.hep.mapper.IndicatorViewBaseInfoMonitorMapper;
import org.dows.hep.service.ExperimentIndicatorViewBaseInfoMonitorRsService;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息监测表(ExperimentIndicatorViewBaseInfoMonitorServiceRsImpl)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("experimentIndicatorViewBaseInfoMonitorService")
public class ExperimentIndicatorViewBaseInfoMonitorServiceRsImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewBaseInfoMonitorRsMapper, ExperimentIndicatorViewBaseInfoMonitorRsEntity> implements ExperimentIndicatorViewBaseInfoMonitorRsService {

}

