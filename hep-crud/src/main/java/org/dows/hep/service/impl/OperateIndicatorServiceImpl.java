package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateIndicatorMapper;
import org.dows.hep.entity.OperateIndicatorEntity;
import org.dows.hep.service.OperateIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 学生操作指标记录表(OperateIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:18
 */
@Service("operateIndicatorService")
public class OperateIndicatorServiceImpl extends MybatisCrudServiceImpl<OperateIndicatorMapper, OperateIndicatorEntity> implements OperateIndicatorService {

}

