package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionSectionMapper;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.service.QuestionSectionService;
import org.springframework.stereotype.Service;


/**
 * 问题集[试卷](QuestionSection)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:17
 */
@Service("questionSectionService")
public class QuestionSectionServiceImpl extends MybatisCrudServiceImpl<QuestionSectionMapper, QuestionSectionEntity> implements QuestionSectionService {

}

