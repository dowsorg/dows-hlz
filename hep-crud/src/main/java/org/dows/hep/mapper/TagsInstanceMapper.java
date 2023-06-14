package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.TagsInstanceEntity;

/**
 * @author jx
 * @date 2023/6/14 15:16
 */
@Mapper
public interface TagsInstanceMapper extends MybatisCrudMapper<TagsInstanceEntity> {
}
