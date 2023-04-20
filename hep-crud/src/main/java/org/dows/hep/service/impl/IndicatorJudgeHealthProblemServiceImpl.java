package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorJudgeHealthProblemMapper;
import org.dows.hep.entity.IndicatorJudgeHealthProblemEntity;
import org.dows.hep.service.IndicatorJudgeHealthProblemService;
import org.springframework.stereotype.Service;


/**
 * 判断指标健康问题(IndicatorJudgeHealthProblem)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:31
 */
@Service("indicatorJudgeHealthProblemService")
public class IndicatorJudgeHealthProblemServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeHealthProblemMapper, IndicatorJudgeHealthProblemEntity> implements IndicatorJudgeHealthProblemService {

}

