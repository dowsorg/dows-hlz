package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorInstanceEntity;
import org.dows.hep.mapper.snapshot.SnapCaseIndicatorInstanceMapper;
import org.dows.hep.service.snapshot.SnapCaseIndicatorInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapCaseIndicatorInstanceService")
public class SnapCaseIndicatorInstanceServiceImpl extends MybatisCrudServiceImpl<SnapCaseIndicatorInstanceMapper, SnapCaseIndicatorInstanceEntity> implements SnapCaseIndicatorInstanceService {

}
