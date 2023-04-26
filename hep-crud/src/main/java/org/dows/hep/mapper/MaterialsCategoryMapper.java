package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.MaterialsCategoryEntity;

/**
 * @author jx
 * @date 2023/4/24 17:06
 */
@Mapper
public interface MaterialsCategoryMapper extends MybatisCrudMapper<MaterialsCategoryEntity> {
}
