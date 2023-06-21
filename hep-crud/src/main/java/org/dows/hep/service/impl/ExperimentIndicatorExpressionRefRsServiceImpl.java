package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRefRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorExpressionItemMapper;
import org.dows.hep.mapper.ExperimentIndicatorExpressionRefRsMapper;
import org.dows.hep.service.ExperimentIndicatorExpressionItemService;
import org.dows.hep.service.ExperimentIndicatorExpressionRefRsService;
import org.springframework.stereotype.Service;


/**
 * 实验指标公式细项(ExperimentIndicatorExpressionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorExpressionRefRsService")
public class ExperimentIndicatorExpressionRefRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorExpressionRefRsMapper, ExperimentIndicatorExpressionRefRsEntity> implements ExperimentIndicatorExpressionRefRsService {

}

