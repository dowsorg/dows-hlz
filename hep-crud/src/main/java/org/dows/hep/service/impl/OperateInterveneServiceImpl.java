package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateInterveneMapper;
import org.dows.hep.entity.OperateInterveneEntity;
import org.dows.hep.service.OperateInterveneService;
import org.springframework.stereotype.Service;


/**
 * 学生干预操作记录(OperateIntervene)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:58:51
 */
@Service("operateInterveneService")
public class OperateInterveneServiceImpl extends MybatisCrudServiceImpl<OperateInterveneMapper, OperateInterveneEntity> implements OperateInterveneService {

}

