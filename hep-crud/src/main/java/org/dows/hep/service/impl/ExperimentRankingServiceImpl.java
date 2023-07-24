package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentRankingMapper;
import org.dows.hep.entity.ExperimentRankingEntity;
import org.dows.hep.service.ExperimentRankingService;
import org.springframework.stereotype.Service;


/**
 * 实验排名(ExperimentRanking)表服务实现类
 *
 * @author lait
 * @since 2023-07-21 15:07:46
 */
@Service("experimentRankingService")
public class ExperimentRankingServiceImpl extends MybatisCrudServiceImpl<ExperimentRankingMapper, ExperimentRankingEntity> implements ExperimentRankingService {

}

