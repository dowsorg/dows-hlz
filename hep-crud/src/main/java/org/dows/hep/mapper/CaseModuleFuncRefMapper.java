package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseOrgModuleFuncRefEntity;

/**
 * 案例模块功能点绑定关系(CaseModuleFuncRefEntity)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:14
 */
@Mapper
public interface CaseModuleFuncRefMapper extends MybatisCrudMapper<CaseOrgModuleFuncRefEntity> {

}

