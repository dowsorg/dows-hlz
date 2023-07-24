package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateCostEntity;

/**
 * 操作花费(OperateCost)表数据库访问层
 *
 * @author lait
 * @since 2023-07-24 10:29:44
 */
@Mapper
public interface OperateCostMapper extends MybatisCrudMapper<OperateCostEntity> {

}

