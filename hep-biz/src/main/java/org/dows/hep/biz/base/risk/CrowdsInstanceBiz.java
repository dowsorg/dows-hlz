package org.dows.hep.biz.base.risk;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.risk.request.CrowdsInstanceRequest;
import org.dows.hep.api.base.risk.request.PageCrowdsRequest;
import org.dows.hep.api.base.risk.response.CrowdsInstanceResponse;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.entity.TagsInstanceEntity;
import org.dows.hep.service.CrowdsInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jx
 * @date 2023/6/15 14:01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CrowdsInstanceBiz {
    private final CrowdsInstanceService crowdsInstanceService;
    private final IdGenerator idGenerator;
    private final IndicatorExpressionBiz indicatorExpressionBiz;

    /**
     * @param
     * @return
     * @说明: 创建或更新人群类别
     * @关联表:
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean insertOrUpdateCrows(CrowdsInstanceRequest crowdsInstanceRequest) {
        Boolean flag = false;
        String crowdsId = crowdsInstanceRequest.getCrowdsId();
        String appId = crowdsInstanceRequest.getAppId();
        //1、更新
        if (StringUtils.isNotBlank(crowdsId)) {
            CrowdsInstanceEntity crowdsEntity = CrowdsInstanceEntity
                    .builder()
                    .id(crowdsInstanceRequest.getId())
                    .crowdsId(crowdsInstanceRequest.getCrowdsId())
                    .appId(crowdsInstanceRequest.getAppId())
                    .name(crowdsInstanceRequest.getName())
                    .crowdsFormulaId(crowdsInstanceRequest.getCrowdsFormulaId())
                    .deathProbability(crowdsInstanceRequest.getDeathProbability())
                    .build();
            flag = crowdsInstanceService.updateById(crowdsEntity);
        } else {
            crowdsId = idGenerator.nextIdStr();
            //2、插入
            CrowdsInstanceEntity crowdsEntity = CrowdsInstanceEntity
                    .builder()
                    .crowdsId(crowdsId)
                    .appId(crowdsInstanceRequest.getAppId())
                    .name(crowdsInstanceRequest.getName())
                    .crowdsFormulaId(crowdsInstanceRequest.getCrowdsFormulaId())
                    .deathProbability(crowdsInstanceRequest.getDeathProbability())
                    .build();
            flag = crowdsInstanceService.save(crowdsEntity);
        }
        List<String> indicatorExpressionIdList = new ArrayList<>();
        indicatorExpressionIdList.add(crowdsInstanceRequest.getCrowdsFormulaId());
        indicatorExpressionBiz.batchBindReasonId(BatchBindReasonIdRequestRs
            .builder()
                .reasonId(crowdsId)
                .appId(appId)
                .source(EnumIndicatorExpressionSource.CROWDS.getType())
                .indicatorExpressionIdList(indicatorExpressionIdList)
            .build());
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 分页获取人群类别
     * @关联表:
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午15:13:34
     */
    public PageResponse<CrowdsInstanceResponse> page(PageCrowdsRequest pageCrowdsRequest) {
        Page page = new Page<TagsInstanceEntity>();
        page.setSize(pageCrowdsRequest.getPageSize());
        page.setCurrent(pageCrowdsRequest.getPageNo());
        if (pageCrowdsRequest.getOrder() != null) {
            String[] array = (String[]) pageCrowdsRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageCrowdsRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }
        try {
            if (!StrUtil.isBlank(pageCrowdsRequest.getKeyword())) {
                page = crowdsInstanceService.page(page, crowdsInstanceService.lambdaQuery()
                        .like(CrowdsInstanceEntity::getName, pageCrowdsRequest.getKeyword())
                        .getWrapper());
            } else {
                page = crowdsInstanceService.page(page, crowdsInstanceService.lambdaQuery().getWrapper());
            }
        } catch (Exception e) {
            throw new ExperimentException(e.getCause().getMessage());
        }
        String appId = pageCrowdsRequest.getAppId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        PageResponse pageInfo = crowdsInstanceService.getPageInfo(page, CrowdsInstanceResponse.class);
        List<CrowdsInstanceResponse> crowdsInstanceResponseList = pageInfo.getList();
        if (Objects.nonNull(crowdsInstanceResponseList) && !crowdsInstanceResponseList.isEmpty()) {
            crowdsInstanceResponseList.forEach(crowsInstanceResponse -> {
                indicatorInstanceIdSet.add(crowsInstanceResponse.getCrowdsId());
            });
            indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
            crowdsInstanceResponseList.forEach(crowsInstanceResponse -> {
                String crowdsId = crowsInstanceResponse.getCrowdsId();
                List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(crowdsId);
                crowsInstanceResponse.setIndicatorExpressionResponseRsList(indicatorExpressionResponseRsList);
            });
        }
        pageInfo.setList(crowdsInstanceResponseList);
        return pageInfo;
    }

    /**
     * @param
     * @return
     * @说明: 查询人群类别
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午15:48:34
     */
    public CrowdsInstanceResponse getCrowdsByCrowdsId(String crowdsId) {
        CrowdsInstanceEntity instanceEntity = crowdsInstanceService.lambdaQuery()
                .eq(CrowdsInstanceEntity::getCrowdsId, crowdsId)
                .eq(CrowdsInstanceEntity::getDeleted, false)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));
        String appId = instanceEntity.getAppId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorInstanceIdSet.add(instanceEntity.getCrowdsId());
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
        List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(instanceEntity.getCrowdsId());
        CrowdsInstanceResponse response = CrowdsInstanceResponse.builder()
                .id(instanceEntity.getId())
                .crowdsId(instanceEntity.getCrowdsId())
                .appId(instanceEntity.getAppId())
                .name(instanceEntity.getName())
                .crowdsFormulaId(instanceEntity.getCrowdsFormulaId())
                .deathProbability(instanceEntity.getDeathProbability())
                .indicatorExpressionResponseRsList(indicatorExpressionResponseRsList)
                .build();
        return response;
    }

    /**
     * @param
     * @return
     * @说明: 删除人群类别
     * @关联表:
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 下午16:02:34
     */
    @DSTransactional
    public Boolean batchDelCrowds(Set<String> crowdsIds) {
        LambdaUpdateWrapper<CrowdsInstanceEntity> updateWrapper = new LambdaUpdateWrapper<CrowdsInstanceEntity>()
                .in(CrowdsInstanceEntity::getCrowdsId, crowdsIds)
                .eq(CrowdsInstanceEntity::getDeleted, false)
                .set(CrowdsInstanceEntity::getDeleted, true);
        return crowdsInstanceService.update(updateWrapper);
    }
}
