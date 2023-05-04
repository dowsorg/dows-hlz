package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentSchemeMapper;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.service.ExperimentSchemeService;
import org.springframework.stereotype.Service;


/**
 * 实验方案(ExperimentScheme)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("experimentSchemeService")
public class ExperimentSchemeServiceImpl extends MybatisCrudServiceImpl<ExperimentSchemeMapper, ExperimentSchemeEntity> implements ExperimentSchemeService {

}

