package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewMonitorFollowupContentRefMapper;
import org.dows.hep.entity.IndicatorViewMonitorFollowupContentRefEntity;
import org.dows.hep.service.IndicatorViewMonitorFollowupContentRefService;
import org.springframework.stereotype.Service;


/**
 * 指标监测随访随访内容表与指标关联关系(IndicatorViewMonitorFollowupContentRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:58:10
 */
@Service("indicatorViewMonitorFollowupContentRefService")
public class IndicatorViewMonitorFollowupContentRefServiceImpl extends MybatisCrudServiceImpl<IndicatorViewMonitorFollowupContentRefMapper, IndicatorViewMonitorFollowupContentRefEntity> implements IndicatorViewMonitorFollowupContentRefService {

}

