package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.mapper.IndicatorExpressionMapper;
import org.dows.hep.mapper.IndicatorExpressionRefMapper;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Service;


/**
 * 指标公式关联(IndicatorExpressionRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorExpressionRefService")
public class IndicatorExpressionRefServiceImpl extends MybatisCrudServiceImpl<IndicatorExpressionRefMapper, IndicatorExpressionRefEntity> implements IndicatorExpressionRefService {

}

