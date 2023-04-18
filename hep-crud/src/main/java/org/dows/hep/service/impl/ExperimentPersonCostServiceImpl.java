package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentPersonCostMapper;
import org.dows.hep.entity.ExperimentPersonCostEntity;
import org.dows.hep.service.ExperimentPersonCostService;
import org.springframework.stereotype.Service;


/**
 * 实验人物资产花费(ExperimentPersonCost)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:55:53
 */
@Service("experimentPersonCostService")
public class ExperimentPersonCostServiceImpl extends MybatisCrudServiceImpl<ExperimentPersonCostMapper, ExperimentPersonCostEntity> implements ExperimentPersonCostService {

}

