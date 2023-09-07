package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.ExperimentEvalLogContentEntity;
import org.dows.hep.mapper.ExperimentEvalLogContentMapper;
import org.dows.hep.service.ExperimentEvalLogContentService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:18
 */
@Service("experimentEvalLogContentService")
public class ExperimentEvalLogContentServiceImpl extends MybatisCrudServiceImpl<ExperimentEvalLogContentMapper, ExperimentEvalLogContentEntity> implements ExperimentEvalLogContentService {
}
