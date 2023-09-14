package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;
import org.dows.hep.mapper.snapshot.SnapRiskModelMapper;
import org.dows.hep.service.snapshot.SnapRiskModelService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/14 10:08
 */
@Service("snapRiskModelService")
public class SnapRiskModelServiceImpl extends MybatisCrudServiceImpl<SnapRiskModelMapper, SnapRiskModelEntity> implements SnapRiskModelService {
}
