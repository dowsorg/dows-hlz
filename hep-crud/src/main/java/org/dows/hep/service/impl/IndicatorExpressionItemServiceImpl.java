package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.mapper.IndicatorExpressionItemMapper;
import org.dows.hep.mapper.IndicatorExpressionMapper;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Service;


/**
 * 指标公式细项(IndicatorExpressionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorExpressionItemService")
public class IndicatorExpressionItemServiceImpl extends MybatisCrudServiceImpl<IndicatorExpressionItemMapper, IndicatorExpressionItemEntity> implements IndicatorExpressionItemService {

}

