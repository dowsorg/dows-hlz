package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;
import org.dows.hep.mapper.snapshot.SnapCrowdsInstanceMapper;
import org.dows.hep.service.snapshot.SnapCrowdsInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/14 10:02
 */

@Service("snapCrowdsInstance")
public class SnapCrowdsInstanceImpl extends MybatisCrudServiceImpl<SnapCrowdsInstanceMapper, SnapCrowdsInstanceEntity> implements SnapCrowdsInstanceService {
}
