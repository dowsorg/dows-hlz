package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseIndicatorValEntity;
import org.dows.hep.entity.IndicatorValEntity;

/**
 * 指标值(IndicatorVal)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface CaseIndicatorValMapper extends MybatisCrudMapper<CaseIndicatorValEntity> {

}

