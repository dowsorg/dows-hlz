package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentEvaluateQuestionnaireEntity;

/**
 * @author fhb
 * @description
 * @date 2023/6/5 19:23
 */
@Mapper
public interface ExperimentEvaluateQuestionnaireMapper extends MybatisCrudMapper<ExperimentEvaluateQuestionnaireEntity> {
}
