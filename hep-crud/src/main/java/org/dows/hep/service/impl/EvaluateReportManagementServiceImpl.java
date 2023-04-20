package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EvaluateReportManagementMapper;
import org.dows.hep.entity.EvaluateReportManagementEntity;
import org.dows.hep.service.EvaluateReportManagementService;
import org.springframework.stereotype.Service;


/**
 * 评估报告管理(EvaluateReportManagement)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:54:33
 */
@Service("evaluateReportManagementService")
public class EvaluateReportManagementServiceImpl extends MybatisCrudServiceImpl<EvaluateReportManagementMapper, EvaluateReportManagementEntity> implements EvaluateReportManagementService {

}

