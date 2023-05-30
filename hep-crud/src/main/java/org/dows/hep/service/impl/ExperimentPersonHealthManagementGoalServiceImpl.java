package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonHealthManagementGoalEntity;
import org.dows.hep.mapper.ExperimentPersonHealthManagementGoalMapper;
import org.dows.hep.service.ExperimentPersonHealthManagementGoalService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/5/29 17:20
 */
@Service("experimentPersonHealthManagementGoalService")
public class ExperimentPersonHealthManagementGoalServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonHealthManagementGoalMapper, ExperimentPersonHealthManagementGoalEntity> implements ExperimentPersonHealthManagementGoalService {
}
