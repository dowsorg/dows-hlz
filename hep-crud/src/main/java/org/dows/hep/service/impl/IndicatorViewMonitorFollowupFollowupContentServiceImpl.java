package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewMonitorFollowupFollowupContentMapper;
import org.dows.hep.entity.IndicatorViewMonitorFollowupFollowupContentEntity;
import org.dows.hep.service.IndicatorViewMonitorFollowupFollowupContentService;
import org.springframework.stereotype.Service;


/**
 * 查看指标监测随访内容(IndicatorViewMonitorFollowupFollowupContent)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorViewMonitorFollowupFollowupContentService")
public class IndicatorViewMonitorFollowupFollowupContentServiceImpl extends MybatisCrudServiceImpl<IndicatorViewMonitorFollowupFollowupContentMapper, IndicatorViewMonitorFollowupFollowupContentEntity> implements IndicatorViewMonitorFollowupFollowupContentService {

}

