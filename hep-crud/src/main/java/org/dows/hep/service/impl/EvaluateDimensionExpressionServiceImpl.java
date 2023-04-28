package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EvaluateDimensionExpressionMapper;
import org.dows.hep.entity.EvaluateDimensionExpressionEntity;
import org.dows.hep.service.EvaluateDimensionExpressionService;
import org.springframework.stereotype.Service;


/**
 * 评估维度公式(EvaluateDimensionExpression)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("evaluateDimensionExpressionService")
public class EvaluateDimensionExpressionServiceImpl extends MybatisCrudServiceImpl<EvaluateDimensionExpressionMapper, EvaluateDimensionExpressionEntity> implements EvaluateDimensionExpressionService {

}

