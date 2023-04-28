package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateFlowEntity;

/**
 * 实验操作流程(OperateFlow)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:51
 */
@Mapper
public interface OperateFlowMapper extends MybatisCrudMapper<OperateFlowEntity> {

}

