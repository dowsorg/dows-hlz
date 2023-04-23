package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewBaseInfoMonitorContentRefMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorContentRefEntity;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorContentRefService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息监测内容表与指标关联关系(IndicatorViewBaseInfoMonitorContentRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("indicatorViewBaseInfoMonitorContentRefService")
public class IndicatorViewBaseInfoMonitorContentRefServiceImpl extends MybatisCrudServiceImpl<IndicatorViewBaseInfoMonitorContentRefMapper, IndicatorViewBaseInfoMonitorContentRefEntity> implements IndicatorViewBaseInfoMonitorContentRefService {

}

