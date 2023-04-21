package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.RiskCategoryMapper;
import org.dows.hep.entity.RiskCategoryEntity;
import org.dows.hep.service.RiskCategoryService;
import org.springframework.stereotype.Service;


/**
 * 风险类别(RiskCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:48
 */
@Service("riskCategoryService")
public class RiskCategoryServiceImpl extends MybatisCrudServiceImpl<RiskCategoryMapper, RiskCategoryEntity> implements RiskCategoryService {

}

