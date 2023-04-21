package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.SportItemIndicatorMapper;
import org.dows.hep.entity.SportItemIndicatorEntity;
import org.dows.hep.service.SportItemIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 运动项目关联指标(SportItemIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:48
 */
@Service("sportItemIndicatorService")
public class SportItemIndicatorServiceImpl extends MybatisCrudServiceImpl<SportItemIndicatorMapper, SportItemIndicatorEntity> implements SportItemIndicatorService {

}

