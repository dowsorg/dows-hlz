package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionSectionItemMapper;
import org.dows.hep.entity.QuestionSectionItemEntity;
import org.dows.hep.service.QuestionSectionItemService;
import org.springframework.stereotype.Service;


/**
 * 问题集[试卷]-题目(QuestionSectionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("questionSectionItemService")
public class QuestionSectionItemServiceImpl extends MybatisCrudServiceImpl<QuestionSectionItemMapper, QuestionSectionItemEntity> implements QuestionSectionItemService {

}

