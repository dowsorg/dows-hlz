package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewBaseInfoDescRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewBaseInfoDescrRsMapper;
import org.dows.hep.service.ExperimentIndicatorViewBaseInfoDescrRsService;
import org.springframework.stereotype.Service;


/**
 * 查看指标基本信息类(IndicatorViewBaseInfo)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("experimentIndicatorViewBaseInfoDescrRsService")
public class ExperimentIndicatorViewBaseInfoDescrRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewBaseInfoDescrRsMapper, ExperimentIndicatorViewBaseInfoDescRsEntity> implements ExperimentIndicatorViewBaseInfoDescrRsService {

}

