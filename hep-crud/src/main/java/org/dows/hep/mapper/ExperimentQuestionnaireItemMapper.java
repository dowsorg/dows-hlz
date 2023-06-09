package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentQuestionnaireItemEntity;

/**
 * @author fhb
 * @description
 * @date 2023/6/6 21:05
 */

@Mapper
public interface ExperimentQuestionnaireItemMapper extends MybatisCrudMapper<ExperimentQuestionnaireItemEntity> {
}
