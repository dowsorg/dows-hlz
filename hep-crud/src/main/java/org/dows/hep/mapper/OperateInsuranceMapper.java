package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateInsuranceEntity;

/**
 * @author jx
 * @date 2023/7/25 15:41
 */
@Mapper
public interface OperateInsuranceMapper extends MybatisCrudMapper<OperateInsuranceEntity> {
}
