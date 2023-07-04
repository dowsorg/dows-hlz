package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapSportItemEntity;
import org.dows.hep.mapper.snapshot.SnapSportItemMapper;
import org.dows.hep.service.snapshot.SnapSportItemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapSportItemService")
public class SnapSportItemServiceImpl extends MybatisCrudServiceImpl<SnapSportItemMapper, SnapSportItemEntity> implements SnapSportItemService {

}
