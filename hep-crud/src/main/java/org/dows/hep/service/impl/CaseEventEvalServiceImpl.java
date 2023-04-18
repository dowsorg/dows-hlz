package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseEventEvalMapper;
import org.dows.hep.entity.CaseEventEvalEntity;
import org.dows.hep.service.CaseEventEvalService;
import org.springframework.stereotype.Service;


/**
 * 案例人物事件触发条件(CaseEventEval)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:54:27
 */
@Service("caseEventEvalService")
public class CaseEventEvalServiceImpl extends MybatisCrudServiceImpl<CaseEventEvalMapper, CaseEventEvalEntity> implements CaseEventEvalService {

}

