package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseSchemeResultEntity;

/**
 * 案例方案结果(CaseSchemeResult)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:32
 */
@Mapper
public interface CaseSchemeResultMapper extends MybatisCrudMapper<CaseSchemeResultEntity> {

}

