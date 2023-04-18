package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionSectionResultMapper;
import org.dows.hep.entity.QuestionSectionResultEntity;
import org.dows.hep.service.QuestionSectionResultService;
import org.springframework.stereotype.Service;


/**
 * 问题集[试卷]-答题记录(QuestionSectionResult)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:31
 */
@Service("questionSectionResultService")
public class QuestionSectionResultServiceImpl extends MybatisCrudServiceImpl<QuestionSectionResultMapper, QuestionSectionResultEntity> implements QuestionSectionResultService {

}

