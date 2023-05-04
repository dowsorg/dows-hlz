package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseOrgEntity;

/**
 * 案例机构(CaseOrg)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:43
 */
@Mapper
public interface CaseOrgMapper extends MybatisCrudMapper<CaseOrgEntity> {

}

