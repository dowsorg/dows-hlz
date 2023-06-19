package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewSupportExamRsEntity;
import org.dows.hep.entity.IndicatorViewSupportExamEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewSupportExamRsMapper;
import org.dows.hep.mapper.IndicatorViewSupportExamMapper;
import org.dows.hep.service.ExperimentIndicatorViewSupportExamRsService;
import org.dows.hep.service.IndicatorViewSupportExamService;
import org.springframework.stereotype.Service;


/**
 * 查看指标辅助检查类(IndicatorViewSupportExam)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentIndicatorViewSupportExamRsService")
public class ExperimentIndicatorViewSupportExamRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewSupportExamRsMapper, ExperimentIndicatorViewSupportExamRsEntity> implements ExperimentIndicatorViewSupportExamRsService {

}

