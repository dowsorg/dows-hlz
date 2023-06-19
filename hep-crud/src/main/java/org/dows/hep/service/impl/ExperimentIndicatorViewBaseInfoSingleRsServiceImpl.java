package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewBaseInfoSingleRsEntity;
import org.dows.hep.entity.IndicatorViewBaseInfoSingleEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewBaseInfoSingleRsMapper;
import org.dows.hep.mapper.IndicatorViewBaseInfoSingleMapper;
import org.dows.hep.service.ExperimentIndicatorViewBaseInfoSingleRsService;
import org.dows.hep.service.IndicatorViewBaseInfoSingleService;
import org.springframework.stereotype.Service;


/**
 * 指标基本信息与单一指标关系表(ExperimentIndicatorViewBaseInfoSingleRsServiceImpl)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("experimentIndicatorViewBaseInfoSingleRsService")
public class ExperimentIndicatorViewBaseInfoSingleRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewBaseInfoSingleRsMapper, ExperimentIndicatorViewBaseInfoSingleRsEntity> implements ExperimentIndicatorViewBaseInfoSingleRsService {

}

