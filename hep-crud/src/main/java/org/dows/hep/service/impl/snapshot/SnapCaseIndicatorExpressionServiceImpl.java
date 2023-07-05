package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.mapper.snapshot.SnapCaseIndicatorExpressionMapper;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapCaseIndicatorExpressionService")
public class SnapCaseIndicatorExpressionServiceImpl extends MybatisCrudServiceImpl<SnapCaseIndicatorExpressionMapper, SnapCaseIndicatorExpressionEntity> implements SnapCaseIndicatorExpressionService {

}
