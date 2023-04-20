package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.InterveneCategoryEntity;

/**
 * 干预类别管理(InterveneCategory)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:31
 */
@Mapper
public interface InterveneCategoryMapper extends MybatisCrudMapper<InterveneCategoryEntity> {

}

