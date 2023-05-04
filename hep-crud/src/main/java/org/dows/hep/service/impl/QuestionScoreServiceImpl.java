package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionScoreMapper;
import org.dows.hep.entity.QuestionScoreEntity;
import org.dows.hep.service.QuestionScoreService;
import org.springframework.stereotype.Service;


/**
 * 问题-得分(QuestionScore)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("questionScoreService")
public class QuestionScoreServiceImpl extends MybatisCrudServiceImpl<QuestionScoreMapper, QuestionScoreEntity> implements QuestionScoreService {

}

