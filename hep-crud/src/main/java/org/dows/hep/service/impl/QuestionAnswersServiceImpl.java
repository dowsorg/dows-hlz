package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionAnswersMapper;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.springframework.stereotype.Service;


/**
 * 问题-答案(QuestionAnswers)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("questionAnswersService")
public class QuestionAnswersServiceImpl extends MybatisCrudServiceImpl<QuestionAnswersMapper, QuestionAnswersEntity> implements QuestionAnswersService {

}

