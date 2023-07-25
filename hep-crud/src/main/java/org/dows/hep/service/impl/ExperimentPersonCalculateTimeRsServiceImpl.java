package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentCaseInfoEntity;
import org.dows.hep.entity.ExperimentPersonCalculateTimeRsEntity;
import org.dows.hep.mapper.ExperimentCaseInfoMapper;
import org.dows.hep.mapper.ExperimentPersonCalculateTimeRsMapper;
import org.dows.hep.service.ExperimentCaseInfoService;
import org.dows.hep.service.ExperimentPersonCalculateTimeRsService;
import org.springframework.stereotype.Service;

/**
 * @author fhb
 * @description
 * @date 2023/5/31 9:24
 */
@Service("experimentPersonCalculateTimeRsService")
public class ExperimentPersonCalculateTimeRsServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonCalculateTimeRsMapper, ExperimentPersonCalculateTimeRsEntity> implements ExperimentPersonCalculateTimeRsService {
}
