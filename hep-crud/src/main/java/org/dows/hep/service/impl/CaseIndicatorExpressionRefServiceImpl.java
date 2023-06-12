package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorExpressionRefEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.mapper.CaseIndicatorExpressionRefMapper;
import org.dows.hep.mapper.IndicatorExpressionRefMapper;
import org.dows.hep.service.CaseIndicatorExpressionRefService;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.springframework.stereotype.Service;


/**
 * 指标公式关联(IndicatorExpressionRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorExpressionRefService")
public class CaseIndicatorExpressionRefServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorExpressionRefMapper, CaseIndicatorExpressionRefEntity> implements CaseIndicatorExpressionRefService {

}

