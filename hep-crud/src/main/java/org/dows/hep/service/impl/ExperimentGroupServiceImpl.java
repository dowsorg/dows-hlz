package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentGroupMapper;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.springframework.stereotype.Service;


/**
 * 实验小组(ExperimentGroup)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:09
 */
@Service("experimentGroupService")
public class ExperimentGroupServiceImpl extends MybatisCrudServiceImpl<ExperimentGroupMapper, ExperimentGroupEntity> implements ExperimentGroupService {

}

