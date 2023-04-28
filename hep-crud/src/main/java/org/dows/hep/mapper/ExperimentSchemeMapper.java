package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSchemeEntity;

/**
 * 实验方案(ExperimentScheme)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:48
 */
@Mapper
public interface ExperimentSchemeMapper extends MybatisCrudMapper<ExperimentSchemeEntity> {

}

