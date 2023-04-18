package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorFuncMapper;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.service.IndicatorFuncService;
import org.springframework.stereotype.Service;


/**
 * 指标功能(IndicatorFunc)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:15
 */
@Service("indicatorFuncService")
public class IndicatorFuncServiceImpl extends MybatisCrudServiceImpl<IndicatorFuncMapper, IndicatorFuncEntity> implements IndicatorFuncService {

}

