package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorCategoryRefEntity;
import org.dows.hep.entity.IndicatorCategoryRefEntity;
import org.dows.hep.mapper.CaseIndicatorCategoryRefMapper;
import org.dows.hep.mapper.IndicatorCategoryRefMapper;
import org.dows.hep.service.CaseIndicatorCategoryRefService;
import org.dows.hep.service.IndicatorCategoryRefService;
import org.springframework.stereotype.Service;


/**
 * 指标分类与指标关联关系(IndicatorCategoryRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorCategoryRefService")
public class CaseIndicatorCategoryRefServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorCategoryRefMapper, CaseIndicatorCategoryRefEntity> implements CaseIndicatorCategoryRefService {

}

