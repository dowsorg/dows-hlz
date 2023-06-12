package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;

/**
 * 指标公式细项(IndicatorExpressionItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface CaseIndicatorExpressionItemMapper extends MybatisCrudMapper<CaseIndicatorExpressionItemEntity> {

}

