package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewSupportExamRefEntity;

/**
 * 查看指标辅助检查关联指标(IndicatorViewSupportExamRef)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:51
 */
@Mapper
public interface IndicatorViewSupportExamRefMapper extends MybatisCrudMapper<IndicatorViewSupportExamRefEntity> {

}

