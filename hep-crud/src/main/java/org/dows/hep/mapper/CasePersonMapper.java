package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CasePersonEntity;

/**
 * 案例人物(CasePerson)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:31
 */
@Mapper
public interface CasePersonMapper extends MybatisCrudMapper<CasePersonEntity> {

}

