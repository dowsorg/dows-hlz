package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorExpressionEntity;
import org.dows.hep.entity.ExperimentIndicatorValEntity;
import org.dows.hep.mapper.ExperimentIndicatorExpressionMapper;
import org.dows.hep.mapper.ExperimentIndicatorValMapper;
import org.dows.hep.service.ExperimentIndicatorExpressionService;
import org.dows.hep.service.ExperimentIndicatorValService;
import org.springframework.stereotype.Service;


/**
 * 实验指标公式(ExperimentIndicatorExpression)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorExpressionService")
public class ExperimentIndicatorExpressionServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorExpressionMapper, ExperimentIndicatorExpressionEntity> implements ExperimentIndicatorExpressionService {

}

