package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapEventCategEntity;
import org.dows.hep.mapper.snapshot.SnapEventCategMapper;
import org.dows.hep.service.snapshot.SnapEventCategService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapEventCategService")
public class SnapEventCategServiceImpl extends MybatisCrudServiceImpl<SnapEventCategMapper, SnapEventCategEntity> implements SnapEventCategService {

}
