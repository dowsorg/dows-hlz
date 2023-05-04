package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateExamMapper;
import org.dows.hep.entity.OperateExamEntity;
import org.dows.hep.service.OperateExamService;
import org.springframework.stereotype.Service;


/**
 * 操作考试[题目]记录(OperateExam)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:17
 */
@Service("operateExamService")
public class OperateExamServiceImpl extends MybatisCrudServiceImpl<OperateExamMapper, OperateExamEntity> implements OperateExamService {

}

