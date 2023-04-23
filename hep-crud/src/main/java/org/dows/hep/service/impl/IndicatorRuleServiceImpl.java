package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorRuleMapper;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.dows.hep.service.IndicatorRuleService;
import org.springframework.stereotype.Service;


/**
 * 指标|变量规则[校验](IndicatorRule)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorRuleService")
public class IndicatorRuleServiceImpl extends MybatisCrudServiceImpl<IndicatorRuleMapper, IndicatorRuleEntity> implements IndicatorRuleService {

}

