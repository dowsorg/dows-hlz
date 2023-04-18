package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionOptionsMapper;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionOptionsService;
import org.springframework.stereotype.Service;


/**
 * 问题-选项(QuestionOptions)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:11
 */
@Service("questionOptionsService")
public class QuestionOptionsServiceImpl extends MybatisCrudServiceImpl<QuestionOptionsMapper, QuestionOptionsEntity> implements QuestionOptionsService {

}

