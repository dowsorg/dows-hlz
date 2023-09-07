package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorLogEntity;
import org.dows.hep.mapper.ExperimentIndicatorLogMapper;
import org.dows.hep.service.ExperimentIndicatorLogService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:17
 */

@Service("experimentIndicatorLogService")
public class ExperimentIndicatorLogServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorLogMapper, ExperimentIndicatorLogEntity> implements ExperimentIndicatorLogService {
}
