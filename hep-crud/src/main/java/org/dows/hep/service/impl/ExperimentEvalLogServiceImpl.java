package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentEvalLogEntity;
import org.dows.hep.mapper.ExperimentEvalLogMapper;
import org.dows.hep.service.ExperimentEvalLogService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:17
 */
@Service("experimentEvalLogService")
public class ExperimentEvalLogServiceImpl extends MybatisCrudServiceImpl<ExperimentEvalLogMapper, ExperimentEvalLogEntity> implements ExperimentEvalLogService {
}
