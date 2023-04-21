package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.MaterialsMapper;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsService;
import org.springframework.stereotype.Service;


/**
 * 资料(Materials)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("materialsService")
public class MaterialsServiceImpl extends MybatisCrudServiceImpl<MaterialsMapper, MaterialsEntity> implements MaterialsService {

}

