package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateInterveneEntity;

/**
 * 学生干预操作记录(OperateIntervene)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:52
 */
@Mapper
public interface OperateInterveneMapper extends MybatisCrudMapper<OperateInterveneEntity> {

}

