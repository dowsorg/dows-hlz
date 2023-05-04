package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewBaseInfoDescrMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoDescrEntity;
import org.dows.hep.service.IndicatorViewBaseInfoDescrService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息描述表(IndicatorViewBaseInfoDescr)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorViewBaseInfoDescrService")
public class IndicatorViewBaseInfoDescrServiceImpl extends MybatisCrudServiceImpl<IndicatorViewBaseInfoDescrMapper, IndicatorViewBaseInfoDescrEntity> implements IndicatorViewBaseInfoDescrService {

}

