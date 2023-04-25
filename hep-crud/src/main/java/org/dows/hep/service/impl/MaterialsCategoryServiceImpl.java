package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.MaterialsCategoryEntity;
import org.dows.hep.mapper.MaterialsCategoryMapper;
import org.dows.hep.service.MaterialsCategoryService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/4/24 17:05
 */
@Service("materialsCategoryService")
public class MaterialsCategoryServiceImpl extends MybatisCrudServiceImpl<MaterialsCategoryMapper, MaterialsCategoryEntity> implements MaterialsCategoryService {
}
