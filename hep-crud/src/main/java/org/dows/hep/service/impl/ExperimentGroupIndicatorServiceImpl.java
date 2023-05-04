package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentGroupIndicatorMapper;
import org.dows.hep.entity.ExperimentGroupIndicatorEntity;
import org.dows.hep.service.ExperimentGroupIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 实验小组指标(ExperimentGroupIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:09
 */
@Service("experimentGroupIndicatorService")
public class ExperimentGroupIndicatorServiceImpl extends MybatisCrudServiceImpl<ExperimentGroupIndicatorMapper, ExperimentGroupIndicatorEntity> implements ExperimentGroupIndicatorService {

}

