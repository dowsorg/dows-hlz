package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapInterveneCategoryEntity;
import org.dows.hep.mapper.snapshot.SnapInterveneCategoryMapper;
import org.dows.hep.service.snapshot.SnapInterveneCategoryService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapInterveneCategoryService")
public class SnapInterveneCategoryServiceImpl extends MybatisCrudServiceImpl<SnapInterveneCategoryMapper, SnapInterveneCategoryEntity> implements SnapInterveneCategoryService {

}
