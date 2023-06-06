package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoDescEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoDescMapper;
import org.dows.hep.service.ExperimentViewBaseInfoDescService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/6 11:22
 */
@Service("experimentViewBaseInfoDescService")
public class ExperimentViewBaseInfoDescServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoDescMapper, ExperimentViewBaseInfoDescEntity> implements ExperimentViewBaseInfoDescService {
}
