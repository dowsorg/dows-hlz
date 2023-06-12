package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorExpressionInfluenceEntity;
import org.dows.hep.entity.IndicatorExpressionInfluenceEntity;
import org.dows.hep.mapper.CaseIndicatorExpressionInfluenceMapper;
import org.dows.hep.mapper.IndicatorExpressionInfluenceMapper;
import org.dows.hep.service.CaseIndicatorExpressionInfluenceService;
import org.dows.hep.service.IndicatorExpressionInfluenceService;
import org.springframework.stereotype.Service;


/**
 * 指标公式影响(IndicatorExpressionInfluenceMapper)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorExpressionInfluenceService")
public class CaseIndicatorExpressionInfluenceServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorExpressionInfluenceMapper, CaseIndicatorExpressionInfluenceEntity> implements CaseIndicatorExpressionInfluenceService {

}

