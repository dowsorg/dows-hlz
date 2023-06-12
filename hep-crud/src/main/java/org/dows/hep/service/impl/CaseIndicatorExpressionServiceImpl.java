package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.mapper.CaseIndicatorExpressionMapper;
import org.dows.hep.mapper.IndicatorExpressionMapper;
import org.dows.hep.service.CaseIndicatorExpressionService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Service;


/**
 * 指标公式(IndicatorExpression)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorExpressionService")
public class CaseIndicatorExpressionServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorExpressionMapper, CaseIndicatorExpressionEntity> implements CaseIndicatorExpressionService {

}

