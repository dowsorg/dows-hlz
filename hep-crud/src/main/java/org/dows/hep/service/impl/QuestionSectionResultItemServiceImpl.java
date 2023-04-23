package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionSectionResultItemMapper;
import org.dows.hep.entity.QuestionSectionResultItemEntity;
import org.dows.hep.service.QuestionSectionResultItemService;
import org.springframework.stereotype.Service;


/**
 * 问题集[试卷]-答题记录Item(QuestionSectionResultItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:48
 */
@Service("questionSectionResultItemService")
public class QuestionSectionResultItemServiceImpl extends MybatisCrudServiceImpl<QuestionSectionResultItemMapper, QuestionSectionResultItemEntity> implements QuestionSectionResultItemService {

}

