package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateFlowSnapMapper;
import org.dows.hep.entity.OperateFlowSnapEntity;
import org.dows.hep.service.OperateFlowSnapService;
import org.springframework.stereotype.Service;


/**
 * 实验操作流程快照(OperateFlowSnap)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("operateFlowSnapService")
public class OperateFlowSnapServiceImpl extends MybatisCrudServiceImpl<OperateFlowSnapMapper, OperateFlowSnapEntity> implements OperateFlowSnapService {

}

