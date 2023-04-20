package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPersonMapper;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.springframework.stereotype.Service;


/**
 * 实验机构人物(ExperimentPerson)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:55:49
 */
@Service("experimentPersonService")
public class ExperimentPersonServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonMapper, ExperimentPersonEntity> implements ExperimentPersonService {

}

