package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorPrincipalRefEntity;

/**
 * 指标主体关联关系(IndicatorPrincipalRef)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:36
 */
@Mapper
public interface IndicatorPrincipalRefMapper extends MybatisCrudMapper<IndicatorPrincipalRefEntity> {

}

