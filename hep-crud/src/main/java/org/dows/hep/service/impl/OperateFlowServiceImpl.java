package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateFlowMapper;
import org.dows.hep.entity.OperateFlowEntity;
import org.dows.hep.service.OperateFlowService;
import org.springframework.stereotype.Service;


/**
 * 实验操作流程(OperateFlow)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:58:45
 */
@Service("operateFlowService")
public class OperateFlowServiceImpl extends MybatisCrudServiceImpl<OperateFlowMapper, OperateFlowEntity> implements OperateFlowService {

}

