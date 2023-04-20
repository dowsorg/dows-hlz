package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorPrincipalRefMapper;
import org.dows.hep.entity.IndicatorPrincipalRefEntity;
import org.dows.hep.service.IndicatorPrincipalRefService;
import org.springframework.stereotype.Service;


/**
 * 指标主体关联关系(IndicatorPrincipalRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:36
 */
@Service("indicatorPrincipalRefService")
public class IndicatorPrincipalRefServiceImpl extends MybatisCrudServiceImpl<IndicatorPrincipalRefMapper, IndicatorPrincipalRefEntity> implements IndicatorPrincipalRefService {

}

