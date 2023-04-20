package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.InterveneCategoryMapper;
import org.dows.hep.entity.InterveneCategoryEntity;
import org.dows.hep.service.InterveneCategoryService;
import org.springframework.stereotype.Service;


/**
 * 干预类别管理(InterveneCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:58:30
 */
@Service("interveneCategoryService")
public class InterveneCategoryServiceImpl extends MybatisCrudServiceImpl<InterveneCategoryMapper, InterveneCategoryEntity> implements InterveneCategoryService {

}

