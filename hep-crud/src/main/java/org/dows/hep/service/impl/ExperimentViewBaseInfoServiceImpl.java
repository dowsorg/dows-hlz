package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentViewBaseInfoEntity;
import org.dows.hep.mapper.ExperimentViewBaseInfoMapper;
import org.dows.hep.service.ExperimentViewBaseInfoService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/13 16:46
 */
@Service("experimentViewBaseInfoService")
public class ExperimentViewBaseInfoServiceImpl extends MybatisCrudServiceImpl<ExperimentViewBaseInfoMapper, ExperimentViewBaseInfoEntity> implements ExperimentViewBaseInfoService {
}
