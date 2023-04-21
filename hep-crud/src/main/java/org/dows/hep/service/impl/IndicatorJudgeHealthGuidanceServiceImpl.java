package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorJudgeHealthGuidanceMapper;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Service;


/**
 * 判断指标健康指导(IndicatorJudgeHealthGuidance)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("indicatorJudgeHealthGuidanceService")
public class IndicatorJudgeHealthGuidanceServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeHealthGuidanceMapper, IndicatorJudgeHealthGuidanceEntity> implements IndicatorJudgeHealthGuidanceService {

}

