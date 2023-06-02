package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorInstanceEntity;
import org.dows.hep.entity.ExperimentIndicatorValEntity;
import org.dows.hep.mapper.ExperimentIndicatorInstanceMapper;
import org.dows.hep.mapper.ExperimentIndicatorValMapper;
import org.dows.hep.service.ExperimentIndicatorInstanceService;
import org.dows.hep.service.ExperimentIndicatorValService;
import org.springframework.stereotype.Service;


/**
 * 实验指标值(ExperimentIndicatorVal)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorValService")
public class ExperimentIndicatorValServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorValMapper, ExperimentIndicatorValEntity> implements ExperimentIndicatorValService {

}

