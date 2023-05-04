package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorJudgeRiskFactorMapper;
import org.dows.hep.entity.IndicatorJudgeRiskFactorEntity;
import org.dows.hep.service.IndicatorJudgeRiskFactorService;
import org.springframework.stereotype.Service;


/**
 * 判断指标危险因素(IndicatorJudgeRiskFactor)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:14
 */
@Service("indicatorJudgeRiskFactorService")
public class IndicatorJudgeRiskFactorServiceImpl extends MybatisCrudServiceImpl<IndicatorJudgeRiskFactorMapper, IndicatorJudgeRiskFactorEntity> implements IndicatorJudgeRiskFactorService {

}

