package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonMedicalResultEntity;
import org.dows.hep.mapper.ExperimentPersonMedicalResultMapper;
import org.dows.hep.service.ExperimentPersonMedicalResultService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/28 11:07
 */
@Service("experimentPersonMedicalResultService")
public class ExperimentPersonMedicalResultServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonMedicalResultMapper, ExperimentPersonMedicalResultEntity> implements ExperimentPersonMedicalResultService {
}
