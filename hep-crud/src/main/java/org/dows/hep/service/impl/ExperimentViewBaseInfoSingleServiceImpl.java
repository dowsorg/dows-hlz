package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoSingleEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoSingleMapper;
import org.dows.hep.service.ExperimentViewBaseInfoSingleService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 11:45
 */
@Service("experimentViewBaseInfoSingleService")
public class ExperimentViewBaseInfoSingleServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoSingleMapper, ExperimentViewBaseInfoSingleEntity> implements ExperimentViewBaseInfoSingleService {
}
