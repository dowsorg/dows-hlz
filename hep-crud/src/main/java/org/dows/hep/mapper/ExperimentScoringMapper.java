package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentScoringEntity;

/**
 * 实验计分(ExperimentScoring)表数据库访问层
 *
 * @author lait
 * @since 2023-07-04 11:31:39
 */
@Mapper
public interface ExperimentScoringMapper extends MybatisCrudMapper<ExperimentScoringEntity> {

}

