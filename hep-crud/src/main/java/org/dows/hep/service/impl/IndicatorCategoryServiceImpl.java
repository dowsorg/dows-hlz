package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorCategoryMapper;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.springframework.stereotype.Service;


/**
 * 指标类别(IndicatorCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorCategoryService")
public class IndicatorCategoryServiceImpl extends MybatisCrudServiceImpl<IndicatorCategoryMapper, IndicatorCategoryEntity> implements IndicatorCategoryService {

}

