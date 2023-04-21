package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewMonitorFollowupMapper;
import org.dows.hep.entity.IndicatorViewMonitorFollowupEntity;
import org.dows.hep.service.IndicatorViewMonitorFollowupService;
import org.springframework.stereotype.Service;


/**
 * 查看指标监测随访类(IndicatorViewMonitorFollowup)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("indicatorViewMonitorFollowupService")
public class IndicatorViewMonitorFollowupServiceImpl extends MybatisCrudServiceImpl<IndicatorViewMonitorFollowupMapper, IndicatorViewMonitorFollowupEntity> implements IndicatorViewMonitorFollowupService {

}

