package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentGroupResourceMapper;
import org.dows.hep.entity.ExperimentGroupResourceEntity;
import org.dows.hep.service.ExperimentGroupResourceService;
import org.springframework.stereotype.Service;


/**
 * 实验小组资源(ExperimentGroupResource)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:10
 */
@Service("experimentGroupResourceService")
public class ExperimentGroupResourceServiceImpl extends MybatisCrudServiceImpl<ExperimentGroupResourceMapper, ExperimentGroupResourceEntity> implements ExperimentGroupResourceService {

}

