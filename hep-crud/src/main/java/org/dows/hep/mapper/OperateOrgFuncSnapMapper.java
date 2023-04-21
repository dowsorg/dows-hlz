package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;

/**
 * 学生机构操作快照(OperateOrgFuncSnap)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:16
 */
@Mapper
public interface OperateOrgFuncSnapMapper extends MybatisCrudMapper<OperateOrgFuncSnapEntity> {

}

