package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
import org.dows.hep.mapper.snapshot.SnapCaseIndicatorExpressionItemMapper;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionItemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapCaseIndicatorExpressionItemService")
public class SnapCaseIndicatorExpressionItemServiceImpl extends MybatisCrudServiceImpl<SnapCaseIndicatorExpressionItemMapper, SnapCaseIndicatorExpressionItemEntity> implements SnapCaseIndicatorExpressionItemService {

}
