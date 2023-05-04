package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentQuestionItemMapper;
import org.dows.hep.entity.ExperimentQuestionItemEntity;
import org.dows.hep.service.ExperimentQuestionItemService;
import org.springframework.stereotype.Service;


/**
 * 实验答题项目(ExperimentQuestionItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:11
 */
@Service("experimentQuestionItemService")
public class ExperimentQuestionItemServiceImpl extends MybatisCrudServiceImpl<ExperimentQuestionItemMapper, ExperimentQuestionItemEntity> implements ExperimentQuestionItemService {

}

