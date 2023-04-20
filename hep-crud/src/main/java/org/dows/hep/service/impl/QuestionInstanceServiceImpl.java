package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.QuestionInstanceMapper;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.service.QuestionInstanceService;
import org.springframework.stereotype.Service;


/**
 * 问题实例(QuestionInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:59:08
 */
@Service("questionInstanceService")
public class QuestionInstanceServiceImpl extends MybatisCrudServiceImpl<QuestionInstanceMapper, QuestionInstanceEntity> implements QuestionInstanceService {

}

