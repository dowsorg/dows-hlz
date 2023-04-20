package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorJudgeDiseaseProblemMapper;
import org.dows.hep.entity.IndicatorJudgeDiseaseProblemEntity;
import org.dows.hep.service.IndicatorJudgeDiseaseProblemService;
import org.springframework.stereotype.Service;


/**
 * 判断指标疾病问题(IndicatorJudgeDiseaseProblem)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:20
 */
@Service("indicatorJudgeDiseaseProblemService")
public class IndicatorJudgeDiseaseProblemServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeDiseaseProblemMapper, IndicatorJudgeDiseaseProblemEntity> implements IndicatorJudgeDiseaseProblemService {

}

