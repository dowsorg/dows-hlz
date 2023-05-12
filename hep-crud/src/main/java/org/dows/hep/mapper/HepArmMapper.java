package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseCategoryEntity;
import org.dows.hep.entity.HepArmEntity;

/**
 * 案例类目(CaseCategory)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:42
 */
@Mapper
public interface HepArmMapper extends MybatisCrudMapper<HepArmEntity> {

}

