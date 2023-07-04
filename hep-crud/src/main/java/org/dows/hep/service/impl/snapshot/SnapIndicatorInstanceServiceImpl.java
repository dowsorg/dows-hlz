package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapIndicatorInstanceEntity;
import org.dows.hep.mapper.snapshot.SnapIndicatorInstanceMapper;
import org.dows.hep.service.snapshot.SnapIndicatorInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapIndicatorInstanceService")
public class SnapIndicatorInstanceServiceImpl extends MybatisCrudServiceImpl<SnapIndicatorInstanceMapper, SnapIndicatorInstanceEntity> implements SnapIndicatorInstanceService {

}
