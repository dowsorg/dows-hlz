package org.dows.hep.biz.base.risk;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskModelRequestRs;
import org.dows.hep.api.base.risk.response.RiskModelResponse;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.RiskModelService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author jx
 * @date 2023/6/15 17:52
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskModelBiz {

    private final RiskModelService riskModelService;

    private final IdGenerator idGenerator;

    /**
     * @param
     * @return
     * @说明: 创建或更新风险模型
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午17:53:34
     */
    @DSTransactional
    public Boolean createOrUpdateRiskModel(CreateOrUpdateRiskModelRequestRs createOrUpdateRiskModelRequestRs) {
        Boolean flag = false;
        if (createOrUpdateRiskModelRequestRs.getId() != null) {
            //1、新增
            RiskModelEntity modelEntity = RiskModelEntity
                    .builder()
                    .id(createOrUpdateRiskModelRequestRs.getId())
                    .riskModelId(createOrUpdateRiskModelRequestRs.getRiskModelId())
                    .appId(createOrUpdateRiskModelRequestRs.getAppId())
                    .name(createOrUpdateRiskModelRequestRs.getName())
                    .riskDeathProbability(createOrUpdateRiskModelRequestRs.getRiskDeathProbability())
                    .crowdsCategoryId(createOrUpdateRiskModelRequestRs.getCrowdsCategoryId())
                    .status(createOrUpdateRiskModelRequestRs.getStatus())
                    .build();
            flag = riskModelService.updateById(modelEntity);
        } else {
            RiskModelEntity modelEntity = RiskModelEntity
                    .builder()
                    .riskModelId(idGenerator.nextIdStr())
                    .appId(createOrUpdateRiskModelRequestRs.getAppId())
                    .name(createOrUpdateRiskModelRequestRs.getName())
                    .riskDeathProbability(createOrUpdateRiskModelRequestRs.getRiskDeathProbability())
                    .crowdsCategoryId(createOrUpdateRiskModelRequestRs.getCrowdsCategoryId())
                    .status(createOrUpdateRiskModelRequestRs.getStatus())
                    .build();
            flag = riskModelService.save(modelEntity);
        }
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 查询风险模型
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午19:02:34
     */
    public RiskModelResponse getRiskModelByRiskModelId(String riskModelId) {
        RiskModelEntity riskModelEntity = riskModelService.lambdaQuery()
                .eq(RiskModelEntity::getRiskModelId, riskModelId)
                .eq(RiskModelEntity::getDeleted, false)
                .one();
        //1、复制属性
        RiskModelResponse modelResponse = RiskModelResponse
                .builder()
                .id(riskModelEntity.getId())
                .riskModelId(riskModelEntity.getRiskModelId())
                .name(riskModelEntity.getName())
                .riskDeathProbability(riskModelEntity.getRiskDeathProbability())
                .crowdsCategoryId(riskModelEntity.getCrowdsCategoryId())
                .status(riskModelEntity.getStatus())
                .build();
        return modelResponse;
    }

    /**
     * @param
     * @return
     * @说明: 查询风险模型
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午19:17:34
     */
    @DSTransactional
    public Boolean batchDelRiskModels(Set<String> riskModelIds) {
        LambdaUpdateWrapper<RiskModelEntity> updateWrapper = new LambdaUpdateWrapper<RiskModelEntity>()
                .in(RiskModelEntity::getRiskModelId, riskModelIds)
                .eq(RiskModelEntity::getDeleted, false)
                .set(RiskModelEntity::getDeleted, true);
        return riskModelService.update(updateWrapper);
    }
}
