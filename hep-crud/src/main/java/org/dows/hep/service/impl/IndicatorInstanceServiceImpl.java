package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorInstanceMapper;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.service.IndicatorInstanceService;
import org.springframework.stereotype.Service;


/**
 * 指标(IndicatorInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorInstanceService")
public class IndicatorInstanceServiceImpl extends MybatisCrudServiceImpl<IndicatorInstanceMapper, IndicatorInstanceEntity> implements IndicatorInstanceService {

}

