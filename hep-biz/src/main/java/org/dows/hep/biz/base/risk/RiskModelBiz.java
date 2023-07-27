package org.dows.hep.biz.base.risk;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskModelRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.risk.request.PageRiskModelRequest;
import org.dows.hep.api.base.risk.response.RiskModelResponse;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.RiskModelService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private final IndicatorExpressionBiz indicatorExpressionBiz;

    private final CrowdsInstanceBiz crowdsInstanceBiz;

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
        String riskModelId = createOrUpdateRiskModelRequestRs.getRiskModelId();
        if (StringUtils.isNotBlank(riskModelId)) {
            //1、新增
            RiskModelEntity modelEntity = RiskModelEntity
                    .builder()
                    .id(createOrUpdateRiskModelRequestRs.getId())
                    .riskModelId(riskModelId)
                    .appId(createOrUpdateRiskModelRequestRs.getAppId())
                    .name(createOrUpdateRiskModelRequestRs.getName())
                    .riskDeathProbability(createOrUpdateRiskModelRequestRs.getRiskDeathProbability())
                    .crowdsCategoryId(createOrUpdateRiskModelRequestRs.getCrowdsCategoryId())
                    .status(createOrUpdateRiskModelRequestRs.getStatus())
                    .build();
            flag = riskModelService.updateById(modelEntity);
        } else {
            riskModelId = idGenerator.nextIdStr();
            RiskModelEntity modelEntity = RiskModelEntity
                    .builder()
                    .riskModelId(riskModelId)
                    .appId(createOrUpdateRiskModelRequestRs.getAppId())
                    .name(createOrUpdateRiskModelRequestRs.getName())
                    .riskDeathProbability(createOrUpdateRiskModelRequestRs.getRiskDeathProbability())
                    .crowdsCategoryId(createOrUpdateRiskModelRequestRs.getCrowdsCategoryId())
                    .status(createOrUpdateRiskModelRequestRs.getStatus())
                    .build();
            flag = riskModelService.save(modelEntity);
        }

        List<String> indicatorExpressionIdList = new ArrayList<>();
        indicatorExpressionIdList.addAll(createOrUpdateRiskModelRequestRs.getRiskModelFormulaIds());
        indicatorExpressionBiz.batchBindReasonId(BatchBindReasonIdRequestRs
                .builder()
                .reasonId(riskModelId)
                .appId(createOrUpdateRiskModelRequestRs.getAppId())
                .source(EnumIndicatorExpressionSource.CROWDS.getSource())
                .indicatorExpressionIdList(indicatorExpressionIdList)
                .build());
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
        String appId = riskModelEntity.getAppId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorInstanceIdSet.add(riskModelEntity.getRiskModelId());
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
        List<IndicatorExpressionResponseRs> indicatorExpressionResponseRs = kReasonIdVIndicatorExpressionResponseRsListMap.get(riskModelEntity.getRiskModelId());
        //1、复制属性
        RiskModelResponse modelResponse = RiskModelResponse
                .builder()
                .id(riskModelEntity.getId())
                .riskModelId(riskModelEntity.getRiskModelId())
                .name(riskModelEntity.getName())
                .riskDeathProbability(riskModelEntity.getRiskDeathProbability())
                .crowdsCategoryId(riskModelEntity.getCrowdsCategoryId())
                .indicatorExpressionResponseRsList(indicatorExpressionResponseRs)
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

    /**
     * @param
     * @return
     * @说明: 更新风险模型状态
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午19:41:34
     */
    @DSTransactional
    public Boolean updateRiskModelStatus(String riskModelId, Integer status) {
        LambdaUpdateWrapper<RiskModelEntity> updateWrapper = new LambdaUpdateWrapper<RiskModelEntity>()
                .eq(RiskModelEntity::getRiskModelId, riskModelId)
                .eq(RiskModelEntity::getDeleted, false)
                .set(RiskModelEntity::getStatus, status);
        return riskModelService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 分页获取风险模型列表
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午19:50:34
     */
    public PageResponse<RiskModelResponse> page(PageRiskModelRequest pageRiskModelRequest) {
        String appId = pageRiskModelRequest.getAppId();
        Page page = new Page<RiskModelEntity>();
        page.setSize(pageRiskModelRequest.getPageSize());
        page.setCurrent(pageRiskModelRequest.getPageNo());
        if (pageRiskModelRequest.getOrder() != null) {
            String[] array = (String[]) pageRiskModelRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageRiskModelRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }
        try {
            if (!StrUtil.isBlank(pageRiskModelRequest.getKeyword())) {
                page = riskModelService.page(page, riskModelService.lambdaQuery()
                        .like(RiskModelEntity::getName, pageRiskModelRequest.getKeyword())
                        .getWrapper());
            } else {
                page = riskModelService.page(page, riskModelService.lambdaQuery().getWrapper());
            }
        } catch (Exception e) {
            throw new ExperimentException(e.getCause().getMessage());
        }
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        PageResponse pageInfo = riskModelService.getPageInfo(page, RiskModelResponse.class);
        List<RiskModelResponse> riskModelResponseList = pageInfo.getList();
        if (Objects.nonNull(riskModelResponseList) && !riskModelResponseList.isEmpty()) {
            riskModelResponseList.forEach(riskModelResponse -> {
                indicatorInstanceIdSet.add(riskModelResponse.getRiskModelId());
            });
            indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
            riskModelResponseList.forEach(riskModelResponse -> {
                String riskModelId = riskModelResponse.getRiskModelId();
                List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(riskModelId);
                riskModelResponse.setIndicatorExpressionResponseRsList(indicatorExpressionResponseRsList);
                riskModelResponse.setCrowdsCategoryName(crowdsInstanceBiz.getCrowdsByCrowdsId(riskModelResponse.getCrowdsCategoryId()).getName());
            });
        }
        pageInfo.setList(riskModelResponseList);
        return pageInfo;
    }
}
