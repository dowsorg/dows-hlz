package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorInstanceEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.mapper.ExperimentIndicatorInstanceMapper;
import org.dows.hep.mapper.IndicatorInstanceMapper;
import org.dows.hep.service.ExperimentIndicatorInstanceService;
import org.dows.hep.service.IndicatorInstanceService;
import org.springframework.stereotype.Service;


/**
 * 实验指标(ExperimentIndicatorInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorInstanceService")
public class ExperimentIndicatorInstanceServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorInstanceMapper, ExperimentIndicatorInstanceEntity> implements ExperimentIndicatorInstanceService {

}

