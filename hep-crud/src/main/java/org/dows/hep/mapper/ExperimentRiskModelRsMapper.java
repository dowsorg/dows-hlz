package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentRiskModelRsEntity;
import org.dows.hep.entity.RiskModelEntity;

/**
 * 风险模型(RiskModel)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:53
 */
@Mapper
public interface ExperimentRiskModelRsMapper extends MybatisCrudMapper<ExperimentRiskModelRsEntity> {

}

