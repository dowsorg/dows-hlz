package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseOrgFunctionEntity;

/**
 * 机构功能(CaseOrgFunction)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:29
 */
@Mapper
public interface CaseOrgFunctionMapper extends MybatisCrudMapper<CaseOrgFunctionEntity> {

}

