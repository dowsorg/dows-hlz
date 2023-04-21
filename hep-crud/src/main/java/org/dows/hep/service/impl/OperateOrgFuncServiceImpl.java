package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateOrgFuncMapper;
import org.dows.hep.entity.OperateOrgFuncEntity;
import org.dows.hep.service.OperateOrgFuncService;
import org.springframework.stereotype.Service;


/**
 * 学生机构操作记录(OperateOrgFunc)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:45
 */
@Service("operateOrgFuncService")
public class OperateOrgFuncServiceImpl extends MybatisCrudServiceImpl<OperateOrgFuncMapper, OperateOrgFuncEntity> implements OperateOrgFuncService {

}

