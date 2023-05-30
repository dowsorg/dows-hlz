package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonHealthGuidanceEntity;
import org.dows.hep.mapper.ExperimentPersonHealthGuidanceMapper;
import org.dows.hep.service.ExperimentPersonHealthGuidanceService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/5/30 14:02
 */
@Service("experimentPersonHealthGuidanceService")
public class ExperimentPersonHealthGuidanceServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonHealthGuidanceMapper, ExperimentPersonHealthGuidanceEntity> implements ExperimentPersonHealthGuidanceService {
}
