package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSchemeScoreEntity;

@Mapper
public interface ExperimentSchemeScoreMapper extends MybatisCrudMapper<ExperimentSchemeScoreEntity> {
}
