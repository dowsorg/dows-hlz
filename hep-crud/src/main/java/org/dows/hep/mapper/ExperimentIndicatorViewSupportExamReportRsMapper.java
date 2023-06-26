package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewPhysicalExamReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorViewSupportExamReportRsEntity;

/**
 * 查看指标体格检查类(ExperimentIndicatorViewSupportExamReportRsMapper)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface ExperimentIndicatorViewSupportExamReportRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewSupportExamReportRsEntity> {

}

