package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseOrgFeeEntity;

/**
 * 案例机构费用(CaseOrgFee)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:14
 */
@Mapper
public interface CaseOrgFeeMapper extends MybatisCrudMapper<CaseOrgFeeEntity> {

}

