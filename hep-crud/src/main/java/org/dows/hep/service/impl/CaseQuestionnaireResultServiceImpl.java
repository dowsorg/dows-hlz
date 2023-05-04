package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseQuestionnaireResultMapper;
import org.dows.hep.entity.CaseQuestionnaireResultEntity;
import org.dows.hep.service.CaseQuestionnaireResultService;
import org.springframework.stereotype.Service;


/**
 * 案例问卷结果(CaseQuestionnaireResult)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("caseQuestionnaireResultService")
public class CaseQuestionnaireResultServiceImpl extends MybatisCrudServiceImpl<CaseQuestionnaireResultMapper, CaseQuestionnaireResultEntity> implements CaseQuestionnaireResultService {

}

