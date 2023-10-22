package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeRiskFactorEntity;
import org.dows.hep.mapper.snapshot.SnapIndicatorJudgeRiskFactorMapper;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeRiskFactorService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:33
 */
@Service("snapIndicatorJudgeRiskFactorService")
public class SnapIndicatorJudgeRiskFactorServiceImpl extends MybatisCrudServiceImpl<SnapIndicatorJudgeRiskFactorMapper, SnapIndicatorJudgeRiskFactorEntity> implements SnapIndicatorJudgeRiskFactorService {

}
