package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentSchemeEntity;

/**
 * 实验方案(ExperimentScheme)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:56:13
 */
@Mapper
public interface ExperimentSchemeMapper extends MybatisCrudMapper<ExperimentSchemeEntity> {

}

