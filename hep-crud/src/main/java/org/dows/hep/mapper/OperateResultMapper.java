package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateResultEntity;

/**
 * 操作结果(OperateResult)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:54
 */
@Mapper
public interface OperateResultMapper extends MybatisCrudMapper<OperateResultEntity> {

}

