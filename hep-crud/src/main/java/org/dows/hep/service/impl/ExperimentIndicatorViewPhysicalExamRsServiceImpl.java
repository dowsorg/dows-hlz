package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentIndicatorViewPhysicalExamRsEntity;
import org.dows.hep.entity.IndicatorViewPhysicalExamEntity;
import org.dows.hep.mapper.ExperimentIndicatorViewPhysicalExamRsMapper;
import org.dows.hep.mapper.IndicatorViewPhysicalExamMapper;
import org.dows.hep.service.ExperimentIndicatorViewPhysicalExamRsService;
import org.dows.hep.service.IndicatorViewPhysicalExamService;
import org.springframework.stereotype.Service;


/**
 * 查看指标体格检查类(IndicatorViewPhysicalExam)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:15
 */
@Service("experimentIndicatorViewPhysicalExamRsService")
public class ExperimentIndicatorViewPhysicalExamRsServiceImpl extends MybatisCrudServiceImpl<ExperimentIndicatorViewPhysicalExamRsMapper, ExperimentIndicatorViewPhysicalExamRsEntity> implements ExperimentIndicatorViewPhysicalExamRsService {

}

