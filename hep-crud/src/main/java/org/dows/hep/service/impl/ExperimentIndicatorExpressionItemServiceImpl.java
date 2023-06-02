package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorExpressionEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemEntity;
import org.dows.hep.mapper.ExperimentIndicatorExpressionItemMapper;
import org.dows.hep.mapper.ExperimentIndicatorExpressionMapper;
import org.dows.hep.service.ExperimentIndicatorExpressionItemService;
import org.dows.hep.service.ExperimentIndicatorExpressionService;
import org.springframework.stereotype.Service;


/**
 * 实验指标公式细项(ExperimentIndicatorExpressionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorExpressionItemService")
public class ExperimentIndicatorExpressionItemServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorExpressionItemMapper, ExperimentIndicatorExpressionItemEntity> implements ExperimentIndicatorExpressionItemService {

}

