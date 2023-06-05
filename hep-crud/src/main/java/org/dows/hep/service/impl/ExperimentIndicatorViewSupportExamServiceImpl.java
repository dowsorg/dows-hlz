package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewSupportExamEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewSupportExamMapper;
import org.dows.hep.service.ExperimentIndicatorViewSupportExamService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/5 16:22
 */
@Service("experimentIndicatorViewSupportExamService")
public class ExperimentIndicatorViewSupportExamServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewSupportExamMapper, ExperimentIndicatorViewSupportExamEntity> implements ExperimentIndicatorViewSupportExamService {
}
