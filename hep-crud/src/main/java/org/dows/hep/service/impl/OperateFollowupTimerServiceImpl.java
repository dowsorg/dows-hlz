package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateFollowupTimerMapper;
import org.dows.hep.entity.OperateFollowupTimerEntity;
import org.dows.hep.service.OperateFollowupTimerService;
import org.springframework.stereotype.Service;


/**
 * 学生随访操作计时器(OperateFollowupTimer)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("operateFollowupTimerService")
public class OperateFollowupTimerServiceImpl extends MybatisCrudServiceImpl<OperateFollowupTimerMapper, OperateFollowupTimerEntity> implements OperateFollowupTimerService {

}

