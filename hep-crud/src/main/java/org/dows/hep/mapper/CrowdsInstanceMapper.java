package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CrowdsInstanceEntity;

/**
 * @author jx
 * @date 2023/6/15 13:57
 */
@Mapper
public interface CrowdsInstanceMapper extends MybatisCrudMapper<CrowdsInstanceEntity> {
}
