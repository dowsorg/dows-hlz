package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorVarMapper;
import org.dows.hep.entity.IndicatorVarEntity;
import org.dows.hep.service.IndicatorVarService;
import org.springframework.stereotype.Service;


/**
 * 指标变量(IndicatorVar)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorVarService")
public class IndicatorVarServiceImpl extends MybatisCrudServiceImpl<IndicatorVarMapper, IndicatorVarEntity> implements IndicatorVarService {

}

