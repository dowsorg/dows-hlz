package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.MaterialsEntity;

/**
 * 资料(Materials)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:33
 */
@Mapper
public interface MaterialsMapper extends MybatisCrudMapper<MaterialsEntity> {

}

