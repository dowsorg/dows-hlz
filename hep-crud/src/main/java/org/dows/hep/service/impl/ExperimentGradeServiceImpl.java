package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentGradeMapper;
import org.dows.hep.entity.ExperimentGradeEntity;
import org.dows.hep.service.ExperimentGradeService;
import org.springframework.stereotype.Service;


/**
 * 实验成绩(ExperimentGrade)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:09
 */
@Service("experimentGradeService")
public class ExperimentGradeServiceImpl extends MybatisCrudServiceImpl<ExperimentGradeMapper, ExperimentGradeEntity> implements ExperimentGradeService {

}

