package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.TreatItemMapper;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.service.TreatItemService;
import org.springframework.stereotype.Service;


/**
 * 治疗项目(TreatItem)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:19
 */
@Service("treatItemService")
public class TreatItemServiceImpl extends MybatisCrudServiceImpl<TreatItemMapper, TreatItemEntity> implements TreatItemService {

}

