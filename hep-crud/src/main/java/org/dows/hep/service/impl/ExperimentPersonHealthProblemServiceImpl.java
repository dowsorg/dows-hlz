package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonHealthProblemEntity;
import org.dows.hep.mapper.ExperimentPersonHealthProblemMapper;
import org.dows.hep.service.ExperimentPersonHealthProblemService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/5/29 14:30
 */
@Service("experimentPersonHealthProblemService")
public class ExperimentPersonHealthProblemServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonHealthProblemMapper, ExperimentPersonHealthProblemEntity> implements ExperimentPersonHealthProblemService {
}
