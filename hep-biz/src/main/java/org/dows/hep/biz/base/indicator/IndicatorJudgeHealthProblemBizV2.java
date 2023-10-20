package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorJudgeHealthProblemRequestRsV2;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponseRsV2;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.IndicatorJudgeHealthProblemException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorJudgeHealthProblemEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorJudgeHealthProblemService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeHealthProblemBizV2 {
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;
    private final IndicatorExpressionBiz indicatorExpressionBiz;

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorJudgeHealthProblemRequestRsV2 createOrUpdateIndicatorJudgeHealthProblemRequestRs) {
        IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity;
        String appId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthProblemRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthProblemRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorJudgeHealthProblemId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorJudgeHealthProblemId();
        BigDecimal point = BigDecimal.valueOf(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getPoint());
        if (StringUtils.isBlank(indicatorJudgeHealthProblemId)) {
            indicatorJudgeHealthProblemId = idGenerator.nextIdStr();
            indicatorJudgeHealthProblemEntity = IndicatorJudgeHealthProblemEntity
                .builder()
                .indicatorJudgeHealthProblemId(indicatorJudgeHealthProblemId)
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .point(point)
                .resultExplain(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getResultExplain())
                .status(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getStatus())
                .build();
        } else {
            String finalIndicatorJudgeHealthProblemId = indicatorJudgeHealthProblemId;
            indicatorJudgeHealthProblemEntity = indicatorJudgeHealthProblemService.lambdaQuery()
                .eq(IndicatorJudgeHealthProblemEntity::getAppId, appId)
                .eq(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthProblemRequestRs indicatorJudgeHealthProblemId:{} is illegal", finalIndicatorJudgeHealthProblemId);
                    throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorJudgeHealthProblemEntity.setName(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getName());
            indicatorJudgeHealthProblemEntity.setStatus(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getStatus());
            indicatorJudgeHealthProblemEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorJudgeHealthProblemEntity.setPoint(point);
            indicatorJudgeHealthProblemEntity.setExpression(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getExpression());
            indicatorJudgeHealthProblemEntity.setResultExplain(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getResultExplain());
        }
        indicatorJudgeHealthProblemService.saveOrUpdate(indicatorJudgeHealthProblemEntity);
        List<String> indicatorExpressionIdList = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorExpressionIdList();
        indicatorExpressionBiz.batchBindReasonId(
                BatchBindReasonIdRequestRs
                        .builder()
                        .reasonId(indicatorJudgeHealthProblemId)
                        .appId(appId)
                        .source(EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_THREE_LEVEL.getSource())
                        .indicatorExpressionIdList(indicatorExpressionIdList)
                        .build());
    }

    public IndicatorJudgeHealthProblemResponseRsV2 getRs(String indicatorJudgeHealthProblemId) {
        IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity = indicatorJudgeHealthProblemService.lambdaQuery()
            .eq(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemId)
            .one();
        if (Objects.isNull(indicatorJudgeHealthProblemEntity)) {
            return null;
        }
        List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList = new ArrayList<>();
        indicatorJudgeHealthProblemEntityList.add(indicatorJudgeHealthProblemEntity);
        List<IndicatorJudgeHealthProblemResponseRsV2> indicatorJudgeHealthProblemResponseRsList = indicatorJudgeHealthProblemEntityList2ResponseRsList(indicatorJudgeHealthProblemEntityList);
        if (indicatorJudgeHealthProblemResponseRsList.isEmpty()) {
            return null;
        }
        return indicatorJudgeHealthProblemResponseRsList.get(0);
    }

    public Page<IndicatorJudgeHealthProblemResponseRsV2> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
        Page<IndicatorJudgeHealthProblemEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityLQW = new LambdaQueryWrapper<>();
        indicatorJudgeHealthProblemEntityLQW
            .eq(Objects.nonNull(appId), IndicatorJudgeHealthProblemEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorJudgeHealthProblemEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(Objects.nonNull(status), IndicatorJudgeHealthProblemEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorJudgeHealthProblemEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
        if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
            Set<String> firstIndicatorCategoryIdSet = Arrays.stream(indicatorCategoryIdList.split(",")).collect(Collectors.toSet());
            Set<String> secondIndicatorCategoryIdSet = getSecondIndicatorCategoryIdSet(firstIndicatorCategoryIdSet);
            /* runsix:if first category list mapped second category list is empty, means nothing */
            if (secondIndicatorCategoryIdSet.isEmpty()) {
                return RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
            } else {
                indicatorJudgeHealthProblemEntityLQW.in(IndicatorJudgeHealthProblemEntity::getIndicatorCategoryId, secondIndicatorCategoryIdSet);
            }
        }
        Page<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityPage = indicatorJudgeHealthProblemService.page(page, indicatorJudgeHealthProblemEntityLQW);
        Page<IndicatorJudgeHealthProblemResponseRsV2> indicatorJudgeHealthProblemResponseRsPage = RsPageUtil.convertFromAnother(indicatorJudgeHealthProblemEntityPage);
        List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList = indicatorJudgeHealthProblemEntityPage.getRecords();
        List<IndicatorJudgeHealthProblemResponseRsV2> indicatorJudgeHealthProblemResponseRsList = indicatorJudgeHealthProblemEntityList2ResponseRsList(indicatorJudgeHealthProblemEntityList);
        indicatorJudgeHealthProblemResponseRsPage.setRecords(indicatorJudgeHealthProblemResponseRsList);
        return indicatorJudgeHealthProblemResponseRsPage;
    }

    private Set<String> getSecondIndicatorCategoryIdSet(Collection<String> firstIndicatorCategoryIdCollection) {
        Set<String> resultSet = new HashSet<>();
        if (Objects.nonNull(firstIndicatorCategoryIdCollection) && !firstIndicatorCategoryIdCollection.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                .in(IndicatorCategoryEntity::getPid, firstIndicatorCategoryIdCollection)
                .list()
                .forEach(indicatorCategoryEntity -> {
                    resultSet.add(indicatorCategoryEntity.getIndicatorCategoryId());
                });
        }
        return resultSet;
    }
    public static IndicatorJudgeHealthProblemResponseRsV2 indicatorJudgeHealthProblem2ResponseRs(
            IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity,
            List<IndicatorCategoryResponse> indicatorCategoryResponseList,
            List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList
    ) {
        return IndicatorJudgeHealthProblemResponseRsV2
                .builder()
                .id(indicatorJudgeHealthProblemEntity.getId())
                .indicatorJudgeHealthProblemId(indicatorJudgeHealthProblemEntity.getIndicatorJudgeHealthProblemId())
                .appId(indicatorJudgeHealthProblemEntity.getAppId())
                .indicatorFuncId(indicatorJudgeHealthProblemEntity.getIndicatorFuncId())
                .name(indicatorJudgeHealthProblemEntity.getName())
                .indicatorCategoryResponseList(indicatorCategoryResponseList)
                .point(indicatorJudgeHealthProblemEntity.getPoint().doubleValue())
                .expression(indicatorJudgeHealthProblemEntity.getExpression())
                .resultExplain(indicatorJudgeHealthProblemEntity.getResultExplain())
                .status(indicatorJudgeHealthProblemEntity.getStatus())
                .dt(indicatorJudgeHealthProblemEntity.getDt())
                .indicatorExpressionResponseRsList(indicatorExpressionResponseRsList)
                .build();
    }

    private List<IndicatorJudgeHealthProblemResponseRsV2> indicatorJudgeHealthProblemEntityList2ResponseRsList(
            List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList
    ) {
        if (Objects.isNull(indicatorJudgeHealthProblemEntityList) || indicatorJudgeHealthProblemEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeHealthProblemEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSetSecond = new HashSet<>();
        Set<String> principalIdSet = new HashSet<>();
        indicatorJudgeHealthProblemEntityList.forEach(
                indicatorJudgeHealthProblemEntity -> {
                    indicatorCategoryIdSetSecond.add(indicatorJudgeHealthProblemEntity.getIndicatorCategoryId());
                    principalIdSet.add(indicatorJudgeHealthProblemEntity.getIndicatorJudgeHealthProblemId());
                });
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdSecondVIndicatorCategoryMap = new HashMap<>();
        Set<String> indicatorCategoryIdSetFirst = new HashSet<>();
        if (!indicatorCategoryIdSetSecond.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                    .eq(IndicatorCategoryEntity::getAppId, appId)
                    .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSetSecond)
                    .list()
                    .forEach(indicatorCategoryEntity -> {
                        indicatorCategoryIdSetFirst.add(indicatorCategoryEntity.getPid());
                        kIndicatorCategoryIdSecondVIndicatorCategoryMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity);
                    });
        }
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdFirstVIndicatorCategoryMap = new HashMap<>();
        if (!indicatorCategoryIdSetFirst.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                    .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSetFirst)
                    .list()
                    .forEach(indicatorCategoryEntity -> {
                        kIndicatorCategoryIdFirstVIndicatorCategoryMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity);
                    });
        }
        //查询指标公式
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdExpressionResponseRsListMap = new HashMap<>();
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, principalIdSet, kReasonIdExpressionResponseRsListMap);

        return indicatorJudgeHealthProblemEntityList
                .stream()
                .map(indicatorJudgeHealthProblemEntity -> {
                    List<IndicatorCategoryResponse> indicatorCategoryResponseList = new ArrayList<>();
                    String indicatorCategoryIdSecond = indicatorJudgeHealthProblemEntity.getIndicatorCategoryId();
                    IndicatorCategoryEntity indicatorCategoryEntitySecond = kIndicatorCategoryIdSecondVIndicatorCategoryMap.get(indicatorCategoryIdSecond);
                    if (Objects.nonNull(indicatorCategoryEntitySecond)) {
                        List<IndicatorCategoryEntity> indicatorCategoryEntityList = new ArrayList<>();
                        String indicatorCategoryIdFirst = indicatorCategoryEntitySecond.getPid();
                        IndicatorCategoryEntity indicatorCategoryEntityFirst= kIndicatorCategoryIdFirstVIndicatorCategoryMap.get(indicatorCategoryIdFirst);
                        if (Objects.nonNull(indicatorCategoryEntityFirst)) {
                            indicatorCategoryEntityList.add(indicatorCategoryEntityFirst);
                        }
                        indicatorCategoryEntityList.add(indicatorCategoryEntitySecond);
                        indicatorCategoryResponseList = indicatorCategoryEntityList.stream().map(IndicatorCategoryBiz::indicatorCategoryEntity2Response).collect(Collectors.toList());
                    }
                    List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdExpressionResponseRsListMap.get(indicatorJudgeHealthProblemEntity.getIndicatorJudgeHealthProblemId());

                    return indicatorJudgeHealthProblem2ResponseRs(
                            indicatorJudgeHealthProblemEntity,
                            indicatorCategoryResponseList,
                            indicatorExpressionResponseRsList
                    );
                })
                .collect(Collectors.toList());
    }
}