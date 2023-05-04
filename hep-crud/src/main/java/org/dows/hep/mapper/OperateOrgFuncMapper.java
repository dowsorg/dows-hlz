package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateOrgFuncEntity;

/**
 * 学生机构操作记录(OperateOrgFunc)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:52
 */
@Mapper
public interface OperateOrgFuncMapper extends MybatisCrudMapper<OperateOrgFuncEntity> {

}

