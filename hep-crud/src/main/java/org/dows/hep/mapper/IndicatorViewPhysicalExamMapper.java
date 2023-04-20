package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewPhysicalExamEntity;

/**
 * 查看指标体格检查类(IndicatorViewPhysicalExam)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:18
 */
@Mapper
public interface IndicatorViewPhysicalExamMapper extends MybatisCrudMapper<IndicatorViewPhysicalExamEntity> {

}

