package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;

/**
 * 判断指标健康指导(IndicatorJudgeHealthGuidance)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:49
 */
@Mapper
public interface IndicatorJudgeHealthGuidanceMapper extends MybatisCrudMapper<IndicatorJudgeHealthGuidanceEntity> {

}

