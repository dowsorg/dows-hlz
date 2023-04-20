package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorRuleEntity;

/**
 * 指标|变量规则[校验](IndicatorRule)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:43
 */
@Mapper
public interface IndicatorRuleMapper extends MybatisCrudMapper<IndicatorRuleEntity> {

}

