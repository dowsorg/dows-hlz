package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewBaseInfoMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoEntity;
import org.dows.hep.service.IndicatorViewBaseInfoService;
import org.springframework.stereotype.Service;


/**
 * 查看指标基本信息类(IndicatorViewBaseInfo)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorViewBaseInfoService")
public class IndicatorViewBaseInfoServiceImpl extends MybatisCrudServiceImpl<IndicatorViewBaseInfoMapper, IndicatorViewBaseInfoEntity> implements IndicatorViewBaseInfoService {

}

