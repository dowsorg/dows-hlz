package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewBaseInfoDescrRefMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoDescrRefEntity;
import org.dows.hep.service.IndicatorViewBaseInfoDescrRefService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息描述表与指标关联关系(IndicatorViewBaseInfoDescrRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorViewBaseInfoDescrRefService")
public class IndicatorViewBaseInfoDescrRefServiceImpl extends MybatisCrudServiceImpl<IndicatorViewBaseInfoDescrRefMapper, IndicatorViewBaseInfoDescrRefEntity> implements IndicatorViewBaseInfoDescrRefService {

}

