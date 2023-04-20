package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.RiskDangerPointEntity;

/**
 * 危险分数(RiskDangerPoint)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:59:42
 */
@Mapper
public interface RiskDangerPointMapper extends MybatisCrudMapper<RiskDangerPointEntity> {

}

