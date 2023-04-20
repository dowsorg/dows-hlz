package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewBaseInfoMonitorContentMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorContentEntity;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorContentService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息监测内容表(IndicatorViewBaseInfoMonitorContent)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:58:00
 */
@Service("indicatorViewBaseInfoMonitorContentService")
public class IndicatorViewBaseInfoMonitorContentServiceImpl extends MybatisCrudServiceImpl<IndicatorViewBaseInfoMonitorContentMapper, IndicatorViewBaseInfoMonitorContentEntity> implements IndicatorViewBaseInfoMonitorContentService {

}

