package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 15:28
 */
@Mapper
public interface ExperimentQuestionnaireMapper extends MybatisCrudMapper<ExperimentQuestionnaireEntity> {
}
