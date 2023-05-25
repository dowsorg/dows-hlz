package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.EvaluateCategoryEntity;
import org.dows.hep.mapper.EvaluateCategoryMapper;
import org.dows.hep.service.EvaluateCategoryService;
import org.springframework.stereotype.Service;

/**
 * 评估类别管理(EvaluateCategory)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("evaluateCategoryService")
public class EvaluateCategoryServiceImpl extends MybatisCrudServiceImpl<EvaluateCategoryMapper, EvaluateCategoryEntity> implements EvaluateCategoryService {
}
