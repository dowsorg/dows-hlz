package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.EvaluateReportManagementEntity;

/**
 * 评估报告管理(EvaluateReportManagement)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:33
 */
@Mapper
public interface EvaluateReportManagementMapper extends MybatisCrudMapper<EvaluateReportManagementEntity> {

}

