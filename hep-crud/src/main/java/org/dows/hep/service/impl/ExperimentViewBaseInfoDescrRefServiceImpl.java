package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoDescrRefEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoDescrRefMapper;
import org.dows.hep.service.ExperimentViewBaseInfoDescrRefService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 14:08
 */
@Service("experimentViewBaseInfoDescrRefService")
public class ExperimentViewBaseInfoDescrRefServiceImpl  extends MybatisCrudServiceImpl<ExperimentViewBaseInfoDescrRefMapper, ExperimentViewBaseInfoDescrRefEntity> implements ExperimentViewBaseInfoDescrRefService {
}
