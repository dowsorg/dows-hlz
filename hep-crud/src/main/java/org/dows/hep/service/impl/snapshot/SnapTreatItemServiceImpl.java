package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapTreatItemEntity;
import org.dows.hep.mapper.snapshot.SnapTreatItemMapper;
import org.dows.hep.service.snapshot.SnapTreatItemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapTreatItemService")
public class SnapTreatItemServiceImpl  extends MybatisCrudServiceImpl<SnapTreatItemMapper, SnapTreatItemEntity> implements SnapTreatItemService {

}
