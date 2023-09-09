package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import org.dows.hep.entity.ExperimentPersonHealthRiskFactorRsEntity;
import org.dows.hep.entity.ExperimentPersonRiskModelRsEntity;
import org.dows.hep.service.ExperimentPersonHealthRiskFactorRsService;
import org.dows.hep.service.ExperimentPersonRiskModelRsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/8 12:15
 */

@Component
@RequiredArgsConstructor
public class EvalPersonDao {
    private final ExperimentPersonRiskModelRsService experimentPersonRiskModelRsService;

    private final ExperimentPersonHealthRiskFactorRsService experimentPersonHealthRiskFactorRsService;

    @Transactional(rollbackFor = Exception.class)
    public boolean saveRisks( List<ExperimentPersonRiskModelRsEntity> experimentPersonRiskModelRsEntityList,
                              List<ExperimentPersonHealthRiskFactorRsEntity> experimentPersonHealthRiskFactorRsEntityList){
        if (!experimentPersonRiskModelRsEntityList.isEmpty()) {
            experimentPersonRiskModelRsService.saveOrUpdateBatch(experimentPersonRiskModelRsEntityList);
        }
        if (!experimentPersonHealthRiskFactorRsEntityList.isEmpty()) {
            experimentPersonHealthRiskFactorRsService.saveOrUpdateBatch(experimentPersonHealthRiskFactorRsEntityList);
        }
        return true;
    }
}
