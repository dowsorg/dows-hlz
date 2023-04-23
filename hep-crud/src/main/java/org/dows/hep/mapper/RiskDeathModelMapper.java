package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.RiskDeathModelEntity;

/**
 * 死亡模型(RiskDeathModel)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:16
 */
@Mapper
public interface RiskDeathModelMapper extends MybatisCrudMapper<RiskDeathModelEntity> {

}

