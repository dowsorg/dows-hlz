package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceRsEntity;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.mapper.ExperimentIndicatorJudgeHealthGuidanceRsMapper;
import org.dows.hep.mapper.IndicatorJudgeHealthGuidanceMapper;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthGuidanceRsService;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Service;


/**
 * 判断指标健康指导(IndicatorJudgeHealthGuidance)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:13
 */
@Service("experimentIndicatorJudgeHealthGuidanceRsService")
public class ExperimentIndicatorJudgeHealthGuidanceRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorJudgeHealthGuidanceRsMapper, ExperimentIndicatorJudgeHealthGuidanceRsEntity> implements ExperimentIndicatorJudgeHealthGuidanceRsService {

}

