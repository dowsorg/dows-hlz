package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateResultMapper;
import org.dows.hep.entity.OperateResultEntity;
import org.dows.hep.service.OperateResultService;
import org.springframework.stereotype.Service;


/**
 * 操作结果(OperateResult)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:45
 */
@Service("operateResultService")
public class OperateResultServiceImpl extends MybatisCrudServiceImpl<OperateResultMapper, OperateResultEntity> implements OperateResultService {

}

