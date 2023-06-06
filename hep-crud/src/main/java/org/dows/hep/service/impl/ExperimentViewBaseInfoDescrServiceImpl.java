package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoDescrEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoDescrMapper;
import org.dows.hep.service.ExperimentViewBaseInfoDescrService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 11:22
 */
@Service("experimentViewBaseInfoDescrService")
public class ExperimentViewBaseInfoDescrServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoDescrMapper, ExperimentViewBaseInfoDescrEntity> implements ExperimentViewBaseInfoDescrService {
}
