package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseOrgQuestionnaireMapper;
import org.dows.hep.entity.CaseOrgQuestionnaireEntity;
import org.dows.hep.service.CaseOrgQuestionnaireService;
import org.springframework.stereotype.Service;


/**
 * 案例机构问卷(CaseOrgQuestionnaire)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("caseOrgQuestionnaireService")
public class CaseOrgQuestionnaireServiceImpl extends MybatisCrudServiceImpl<CaseOrgQuestionnaireMapper, CaseOrgQuestionnaireEntity> implements CaseOrgQuestionnaireService {

}

