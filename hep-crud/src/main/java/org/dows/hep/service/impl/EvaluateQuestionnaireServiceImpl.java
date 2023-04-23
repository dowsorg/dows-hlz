package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.EvaluateQuestionnaireMapper;
import org.dows.hep.entity.EvaluateQuestionnaireEntity;
import org.dows.hep.service.EvaluateQuestionnaireService;
import org.springframework.stereotype.Service;


/**
 * 评估问卷(EvaluateQuestionnaire)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("evaluateQuestionnaireService")
public class EvaluateQuestionnaireServiceImpl extends MybatisCrudServiceImpl<EvaluateQuestionnaireMapper, EvaluateQuestionnaireEntity> implements EvaluateQuestionnaireService {

}

