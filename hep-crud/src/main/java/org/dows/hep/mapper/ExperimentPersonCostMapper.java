package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonCostEntity;

/**
 * 实验人物资产花费(ExperimentPersonCost)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:46
 */
@Mapper
public interface ExperimentPersonCostMapper extends MybatisCrudMapper<ExperimentPersonCostEntity> {

}

