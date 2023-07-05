package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionRefEntity;
import org.dows.hep.mapper.snapshot.SnapCaseIndicatorExpressionRefMapper;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionRefService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapCaseIndicatorExpressionRefService")
public class SnapCaseIndicatorExpressionRefServiceImpl extends MybatisCrudServiceImpl<SnapCaseIndicatorExpressionRefMapper, SnapCaseIndicatorExpressionRefEntity> implements SnapCaseIndicatorExpressionRefService {

}
