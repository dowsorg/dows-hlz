package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentSchemeItemEntity;
import org.dows.hep.mapper.ExperimentSchemeItemMapper;
import org.dows.hep.service.ExperimentSchemeItemService;
import org.springframework.stereotype.Service;

@Service("experimentSchemeItemService")
public class ExperimentSchemeItemServiceImpl extends MybatisCrudServiceImpl<ExperimentSchemeItemMapper, ExperimentSchemeItemEntity> implements ExperimentSchemeItemService {
}
