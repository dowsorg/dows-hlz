package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseCategoryMapper;
import org.dows.hep.entity.CaseCategoryEntity;
import org.dows.hep.service.CaseCategoryService;
import org.springframework.stereotype.Service;


/**
 * 案例类目(CaseCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:40
 */
@Service("caseCategoryService")
public class CaseCategoryServiceImpl extends MybatisCrudServiceImpl<CaseCategoryMapper, CaseCategoryEntity> implements CaseCategoryService {

}

