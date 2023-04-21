package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewBaseInfoMonitorMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorEntity;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息监测表(IndicatorViewBaseInfoMonitor)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorViewBaseInfoMonitorService")
public class IndicatorViewBaseInfoMonitorServiceImpl extends MybatisCrudServiceImpl<IndicatorViewBaseInfoMonitorMapper, IndicatorViewBaseInfoMonitorEntity> implements IndicatorViewBaseInfoMonitorService {

}

