package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRsV2;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponseRsV2;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.IndicatorJudgeHealthGuidanceException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:指标:判断指标-健康指导
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeHealthGuidanceBizV2 {
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;
    private final IndicatorExpressionBiz indicatorExpressionBiz;

    /**
     * 新建健康指导
     */
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRsV2 createOrUpdateIndicatorJudgeHealthGuidanceRequestRs) throws InterruptedException {
        IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity;
        String appId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                    .eq(IndicatorFuncEntity::getAppId, appId)
                    .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                    .oneOpt()
                    .orElseThrow(() -> {
                        log.warn("method IndicatorJudgeHealthGuidanceBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthGuidanceRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                        return new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
                    });
        }
        String indicatorCategoryId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                    .eq(IndicatorCategoryEntity::getAppId, appId)
                    .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                    .oneOpt()
                    .orElseThrow(() -> {
                        log.warn("method IndicatorJudgeHealthGuidanceBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthGuidanceRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                        return new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
                    });
        }
        String indicatorJudgeHealthGuidanceId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorJudgeHealthGuidanceId();
        BigDecimal point = BigDecimal.valueOf(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getPoint());
        if (StringUtils.isBlank(indicatorJudgeHealthGuidanceId)) {
            indicatorJudgeHealthGuidanceEntity = IndicatorJudgeHealthGuidanceEntity
                    .builder()
                    .indicatorJudgeHealthGuidanceId(idGenerator.nextIdStr())
                    .appId(appId)
                    .indicatorFuncId(indicatorFuncId)
                    .name(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getName())
                    .indicatorCategoryId(indicatorCategoryId)
                    .point(point)
                    .resultExplain(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getResultExplain())
                    .status(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getStatus())
                    .build();
        } else {
            indicatorJudgeHealthGuidanceEntity = indicatorJudgeHealthGuidanceService.lambdaQuery()
                    .eq(IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
                    .eq(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, indicatorJudgeHealthGuidanceId)
                    .oneOpt()
                    .orElseThrow(() -> {
                        log.warn("method IndicatorJudgeHealthGuidanceBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthGuidanceRequestRs indicatorJudgeHealthGuidanceId:{} is illegal", indicatorJudgeHealthGuidanceId);
                        return new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
                    });
            indicatorJudgeHealthGuidanceEntity.setName(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getName());
            indicatorJudgeHealthGuidanceEntity.setStatus(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getStatus());
            indicatorJudgeHealthGuidanceEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorJudgeHealthGuidanceEntity.setPoint(point);
            indicatorJudgeHealthGuidanceEntity.setExpression(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getExpression());
            indicatorJudgeHealthGuidanceEntity.setResultExplain(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getResultExplain());
        }
        indicatorJudgeHealthGuidanceService.saveOrUpdate(indicatorJudgeHealthGuidanceEntity);
        List<String> indicatorExpressionIdList = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorExpressionIdList();
        indicatorExpressionBiz.batchBindReasonId(
                BatchBindReasonIdRequestRs
                        .builder()
                        .reasonId(indicatorJudgeHealthGuidanceId)
                        .appId(appId)
                        .source(EnumIndicatorExpressionSource.INDICATOR_JUDGE_RISK_FACTOR.getSource())
                        .indicatorExpressionIdList(indicatorExpressionIdList)
                        .build());
    }

    /**
     * 详情查询
     */
    public IndicatorJudgeHealthGuidanceResponseRsV2 getRs(String indicatorJudgeHealthGuidanceId) {
        IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity = indicatorJudgeHealthGuidanceService.lambdaQuery()
                .eq(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, indicatorJudgeHealthGuidanceId)
                .one();
        if (Objects.isNull(indicatorJudgeHealthGuidanceEntity)) {
            return null;
        }
        List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList = new ArrayList<>();
        indicatorJudgeHealthGuidanceEntityList.add(indicatorJudgeHealthGuidanceEntity);
        List<IndicatorJudgeHealthGuidanceResponseRsV2> indicatorJudgeHealthGuidanceResponseRsList = indicatorJudgeHealthGuidanceEntityList2ResponseRsList(indicatorJudgeHealthGuidanceEntityList);
        if (indicatorJudgeHealthGuidanceResponseRsList.isEmpty()) {
            return null;
        }
        return indicatorJudgeHealthGuidanceResponseRsList.get(0);
    }

    /**
     * 分页查询
     */
    public Page<IndicatorJudgeHealthGuidanceResponseRsV2> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
        Page<IndicatorJudgeHealthGuidanceEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityLQW = new LambdaQueryWrapper<>();
        indicatorJudgeHealthGuidanceEntityLQW
                .eq(Objects.nonNull(appId), IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
                .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorJudgeHealthGuidanceEntity::getIndicatorFuncId, indicatorFuncId)
                .eq(Objects.nonNull(status), IndicatorJudgeHealthGuidanceEntity::getStatus, status)
                .like(StringUtils.isNotBlank(name), IndicatorJudgeHealthGuidanceEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
        if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
            Set<String> firstIndicatorCategoryIdSet = Arrays.stream(indicatorCategoryIdList.split(",")).collect(Collectors.toSet());
            if (firstIndicatorCategoryIdSet.isEmpty()) {
                return RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
            } else {
                indicatorJudgeHealthGuidanceEntityLQW.in(IndicatorJudgeHealthGuidanceEntity::getIndicatorCategoryId, firstIndicatorCategoryIdSet);
            }
        }
        Page<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityPage = indicatorJudgeHealthGuidanceService.page(page, indicatorJudgeHealthGuidanceEntityLQW);
        Page<IndicatorJudgeHealthGuidanceResponseRsV2> indicatorJudgeHealthGuidanceResponseRsPage = RsPageUtil.convertFromAnother(indicatorJudgeHealthGuidanceEntityPage);
        List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList = indicatorJudgeHealthGuidanceEntityPage.getRecords();
        List<IndicatorJudgeHealthGuidanceResponseRsV2> indicatorJudgeHealthGuidanceResponseRsList = indicatorJudgeHealthGuidanceEntityList2ResponseRsList(indicatorJudgeHealthGuidanceEntityList);
        indicatorJudgeHealthGuidanceResponseRsPage.setRecords(indicatorJudgeHealthGuidanceResponseRsList);
        return indicatorJudgeHealthGuidanceResponseRsPage;
    }

    public static IndicatorJudgeHealthGuidanceResponseRsV2 indicatorJudgeHealthGuidance2ResponseRs(
            IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity,
            IndicatorCategoryResponse indicatorCategoryResponse,
            List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList
    ) {
        return IndicatorJudgeHealthGuidanceResponseRsV2
                .builder()
                .id(indicatorJudgeHealthGuidanceEntity.getId())
                .indicatorJudgeHealthGuidanceId(indicatorJudgeHealthGuidanceEntity.getIndicatorJudgeHealthGuidanceId())
                .appId(indicatorJudgeHealthGuidanceEntity.getAppId())
                .indicatorFuncId(indicatorJudgeHealthGuidanceEntity.getIndicatorFuncId())
                .name(indicatorJudgeHealthGuidanceEntity.getName())
                .indicatorCategoryResponse(indicatorCategoryResponse)
                .point(indicatorJudgeHealthGuidanceEntity.getPoint().doubleValue())
                .expression(indicatorJudgeHealthGuidanceEntity.getExpression())
                .resultExplain(indicatorJudgeHealthGuidanceEntity.getResultExplain())
                .status(indicatorJudgeHealthGuidanceEntity.getStatus())
                .dt(indicatorJudgeHealthGuidanceEntity.getDt())
                .indicatorExpressionResponseRsList(indicatorExpressionResponseRsList)
                .build();
    }

    private List<IndicatorJudgeHealthGuidanceResponseRsV2> indicatorJudgeHealthGuidanceEntityList2ResponseRsList(
            List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList
    ) {
        if (Objects.isNull(indicatorJudgeHealthGuidanceEntityList) || indicatorJudgeHealthGuidanceEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeHealthGuidanceEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        Set<String> principalIdSet = new HashSet<>();
        indicatorJudgeHealthGuidanceEntityList.forEach(
                indicatorJudgeHealthGuidanceEntity -> {
                    indicatorCategoryIdSet.add(indicatorJudgeHealthGuidanceEntity.getIndicatorCategoryId());
                    principalIdSet.add(indicatorJudgeHealthGuidanceEntity.getIndicatorJudgeHealthGuidanceId());
                });
        Map<String, IndicatorCategoryResponse> kIndicatorCategoryIdVIndicatorCategoryResponseMap = new HashMap<>();
        if (!indicatorCategoryIdSet.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                    .eq(IndicatorCategoryEntity::getAppId, appId)
                    .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
                    .list()
                    .stream()
                    .map(IndicatorCategoryBiz::indicatorCategoryEntity2Response).filter(Objects::nonNull)
                    .forEach(indicatorCategoryResponse -> kIndicatorCategoryIdVIndicatorCategoryResponseMap.put(
                            indicatorCategoryResponse.getIndicatorCategoryId(), indicatorCategoryResponse
                    ));
        }
        //查询指标公式
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdExpressionResponseRsListMap = new HashMap<>();
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, principalIdSet, kReasonIdExpressionResponseRsListMap);

        return indicatorJudgeHealthGuidanceEntityList
                .stream()
                .map(indicatorJudgeHealthGuidanceEntity -> {
                    IndicatorCategoryResponse indicatorCategoryResponse = kIndicatorCategoryIdVIndicatorCategoryResponseMap.get(indicatorJudgeHealthGuidanceEntity.getIndicatorCategoryId());
                    List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdExpressionResponseRsListMap.get(indicatorJudgeHealthGuidanceEntity.getIndicatorJudgeHealthGuidanceId());
                    return indicatorJudgeHealthGuidance2ResponseRs(
                            indicatorJudgeHealthGuidanceEntity,
                            indicatorCategoryResponse,
                            indicatorExpressionResponseRsList
                    );
                })
                .collect(Collectors.toList());
    }
}