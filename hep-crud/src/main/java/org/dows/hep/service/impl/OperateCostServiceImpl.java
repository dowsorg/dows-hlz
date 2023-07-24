package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.OperateCostMapper;
import org.dows.hep.entity.OperateCostEntity;
import org.dows.hep.service.OperateCostService;
import org.springframework.stereotype.Service;


/**
 * 操作花费(OperateCost)表服务实现类
 *
 * @author lait
 * @since 2023-07-24 10:29:44
 */
@Service("operateCostService")
public class OperateCostServiceImpl extends MybatisCrudServiceImpl<OperateCostMapper, OperateCostEntity> implements OperateCostService {

}

