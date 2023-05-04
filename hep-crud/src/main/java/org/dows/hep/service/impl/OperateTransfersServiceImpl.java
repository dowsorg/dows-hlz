package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateTransfersMapper;
import org.dows.hep.entity.OperateTransfersEntity;
import org.dows.hep.service.OperateTransfersService;
import org.springframework.stereotype.Service;


/**
 * 操作机构转入转出记录(OperateTransfers)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("operateTransfersService")
public class OperateTransfersServiceImpl extends MybatisCrudServiceImpl<OperateTransfersMapper, OperateTransfersEntity> implements OperateTransfersService {

}

