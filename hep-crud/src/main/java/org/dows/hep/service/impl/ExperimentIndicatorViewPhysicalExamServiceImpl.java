package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewPhysicalExamEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewPhysicalExamMapper;
import org.dows.hep.service.ExperimentIndicatorViewPhysicalExamService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 16:14
 */
@Service("experimentIndicatorViewPhysicalExamService")
public class ExperimentIndicatorViewPhysicalExamServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewPhysicalExamMapper, ExperimentIndicatorViewPhysicalExamEntity> implements ExperimentIndicatorViewPhysicalExamService {
}
