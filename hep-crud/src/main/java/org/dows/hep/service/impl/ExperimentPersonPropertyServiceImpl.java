package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPersonPropertyMapper;
import org.dows.hep.entity.ExperimentPersonPropertyEntity;
import org.dows.hep.service.ExperimentPersonPropertyService;
import org.springframework.stereotype.Service;


/**
 * 实验人物数据(ExperimentPersonProperty)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:11
 */
@Service("experimentPersonPropertyService")
public class ExperimentPersonPropertyServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonPropertyMapper, ExperimentPersonPropertyEntity> implements ExperimentPersonPropertyService {

}

