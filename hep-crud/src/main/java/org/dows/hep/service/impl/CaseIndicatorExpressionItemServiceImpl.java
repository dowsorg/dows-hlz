package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.mapper.CaseIndicatorExpressionItemMapper;
import org.dows.hep.mapper.IndicatorExpressionItemMapper;
import org.dows.hep.service.CaseIndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.springframework.stereotype.Service;


/**
 * 指标公式细项(IndicatorExpressionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorExpressionItemService")
public class CaseIndicatorExpressionItemServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorExpressionItemMapper, CaseIndicatorExpressionItemEntity> implements CaseIndicatorExpressionItemService {

}

