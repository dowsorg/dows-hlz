package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentRankingEntity;

/**
 * 实验排名(ExperimentRanking)表数据库访问层
 *
 * @author lait
 * @since 2023-07-21 15:07:46
 */
@Mapper
public interface ExperimentRankingMapper extends MybatisCrudMapper<ExperimentRankingEntity> {

}

