package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentMaterialsMapper;
import org.dows.hep.entity.ExperimentMaterialsEntity;
import org.dows.hep.service.ExperimentMaterialsService;
import org.springframework.stereotype.Service;


/**
 * 实验资料(ExperimentMaterials)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:10
 */
@Service("experimentMaterialsService")
public class ExperimentMaterialsServiceImpl extends MybatisCrudServiceImpl<ExperimentMaterialsMapper, ExperimentMaterialsEntity> implements ExperimentMaterialsService {

}

