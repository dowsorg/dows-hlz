package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.mapper.ExperimentOrgMapper;
import org.dows.hep.service.ExperimentOrgService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/5/8 20:38
 */
@Service("experimentOrgService")
public class ExperimentOrgServiceImpl extends MybatisCrudServiceImpl<ExperimentOrgMapper, ExperimentOrgEntity> implements ExperimentOrgService {
}
