package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorRefMapper;
import org.dows.hep.entity.IndicatorRefEntity;
import org.dows.hep.service.IndicatorRefService;
import org.springframework.stereotype.Service;


/**
 * 指标-引用(IndicatorRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorRefService")
public class IndicatorRefServiceImpl extends MybatisCrudServiceImpl<IndicatorRefMapper, IndicatorRefEntity> implements IndicatorRefService {

}

