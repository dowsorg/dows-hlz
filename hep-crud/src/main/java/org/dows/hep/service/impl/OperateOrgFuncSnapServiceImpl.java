package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateOrgFuncSnapMapper;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;
import org.dows.hep.service.OperateOrgFuncSnapService;
import org.springframework.stereotype.Service;


/**
 * 学生机构操作快照(OperateOrgFuncSnap)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("operateOrgFuncSnapService")
public class OperateOrgFuncSnapServiceImpl extends MybatisCrudServiceImpl<OperateOrgFuncSnapMapper, OperateOrgFuncSnapEntity> implements OperateOrgFuncSnapService {

}

