package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorExpressionInfluenceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemRsEntity;
import org.dows.hep.mapper.ExperimentIndicatorExpressionInfluenceRsMapper;
import org.dows.hep.mapper.ExperimentIndicatorExpressionItemRsMapper;
import org.dows.hep.service.ExperimentIndicatorExpressionInfluenceRsService;
import org.dows.hep.service.ExperimentIndicatorExpressionItemRsService;
import org.springframework.stereotype.Service;


/**
 * 实验指标公式细项(ExperimentIndicatorExpressionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("experimentIndicatorExpressionInfluenceRsService")
public class ExperimentIndicatorExpressionInfluenceRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorExpressionInfluenceRsMapper, ExperimentIndicatorExpressionInfluenceRsEntity> implements ExperimentIndicatorExpressionInfluenceRsService {

}

