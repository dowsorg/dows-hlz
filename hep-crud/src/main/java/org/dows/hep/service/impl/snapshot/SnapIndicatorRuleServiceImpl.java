package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapIndicatorRuleEntity;
import org.dows.hep.mapper.snapshot.SnapIndicatorRuleMapper;
import org.dows.hep.service.snapshot.SnapIndicatorRuleService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapIndicatorRuleService")
public class SnapIndicatorRuleServiceImpl extends MybatisCrudServiceImpl<SnapIndicatorRuleMapper, SnapIndicatorRuleEntity> implements SnapIndicatorRuleService {

}
