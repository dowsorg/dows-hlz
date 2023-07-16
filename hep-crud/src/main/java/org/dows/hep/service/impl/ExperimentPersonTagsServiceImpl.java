package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentPersonTagsEntity;
import org.dows.hep.mapper.ExperimentPersonTagsMapper;
import org.dows.hep.service.ExperimentPersonTagsService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/7/13 17:04
 */
@Service("experimentPersonTagsService")
public class ExperimentPersonTagsServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonTagsMapper, ExperimentPersonTagsEntity> implements ExperimentPersonTagsService {
}
