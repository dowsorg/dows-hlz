package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorValMapper;
import org.dows.hep.entity.IndicatorValEntity;
import org.dows.hep.service.IndicatorValService;
import org.springframework.stereotype.Service;


/**
 * 指标值(IndicatorVal)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:45
 */
@Service("indicatorValService")
public class IndicatorValServiceImpl extends MybatisCrudServiceImpl<IndicatorValMapper, IndicatorValEntity> implements IndicatorValService {

}

