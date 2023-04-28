package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateIndictarMapper;
import org.dows.hep.entity.OperateIndictarEntity;
import org.dows.hep.service.OperateIndictarService;
import org.springframework.stereotype.Service;


/**
 * 学生操作指标记录表(OperateIndictar)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("operateIndictarService")
public class OperateIndictarServiceImpl extends MybatisCrudServiceImpl<OperateIndictarMapper, OperateIndictarEntity> implements OperateIndictarService {

}

