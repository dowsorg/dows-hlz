package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSchemeScoreItemEntity;

@Mapper
public interface ExperimentSchemeScoreItemMapper extends MybatisCrudMapper<ExperimentSchemeScoreItemEntity> {
}
