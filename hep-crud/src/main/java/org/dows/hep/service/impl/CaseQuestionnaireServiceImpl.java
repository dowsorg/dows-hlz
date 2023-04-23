package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseQuestionnaireMapper;
import org.dows.hep.entity.CaseQuestionnaireEntity;
import org.dows.hep.service.CaseQuestionnaireService;
import org.springframework.stereotype.Service;


/**
 * 案例问卷(CaseQuestionnaire)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("caseQuestionnaireService")
public class CaseQuestionnaireServiceImpl extends MybatisCrudServiceImpl<CaseQuestionnaireMapper, CaseQuestionnaireEntity> implements CaseQuestionnaireService {

}

