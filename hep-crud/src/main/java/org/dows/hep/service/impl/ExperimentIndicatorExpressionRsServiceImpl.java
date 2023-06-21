package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorExpressionRefRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorExpressionRefRsMapper;
import org.dows.hep.mapper.ExperimentIndicatorExpressionRsMapper;
import org.dows.hep.service.ExperimentIndicatorExpressionRefRsService;
import org.dows.hep.service.ExperimentIndicatorExpressionRsService;
import org.springframework.stereotype.Service;


/**
 * 实验指标公式细项(ExperimentIndicatorExpressionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorExpressionRsService")
public class ExperimentIndicatorExpressionRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorExpressionRsMapper, ExperimentIndicatorExpressionRsEntity> implements ExperimentIndicatorExpressionRsService {

}

