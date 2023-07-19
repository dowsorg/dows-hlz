package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.exception.IndicatorCategoryException;
import org.dows.hep.api.exception.RsIndicatorInstanceBizException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsIndicatorInstanceBiz {
  private final IndicatorCategoryService indicatorCategoryService;
  private final IndicatorInstanceService indicatorInstanceService;
  private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;
  private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;
  private final IndicatorViewBaseInfoSingleService indicatorViewBaseInfoSingleService;
  private final IndicatorViewMonitorFollowupService indicatorViewMonitorFollowupService;
  private final IndicatorViewMonitorFollowupContentRefService indicatorViewMonitorFollowupContentRefService;
  private final IndicatorViewPhysicalExamService indicatorViewPhysicalExamService;
  private final IndicatorViewSupportExamService indicatorViewSupportExamService;
  private final IndicatorJudgeRiskFactorService indicatorJudgeRiskFactorService;
  private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;
  private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;
  private final IndicatorExpressionService indicatorExpressionService;
  private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
  private final TagsInstanceService tagsInstanceService;
  private final RsUtilBiz rsUtilBiz;
  private final IndicatorRuleService indicatorRuleService;

  public void checkIndicatorInstanceDeleteInIndicatorViewBaseInfoDescrRef(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = indicatorViewBaseInfoDescrRefService.lambdaQuery()
        .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
        .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list();
    if (!indicatorViewBaseInfoDescrRefEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_VIEW_BASE_INFO_DESCR_REF_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorViewBaseInfoMonitorContentRef(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
        .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list();
    if (!indicatorViewBaseInfoMonitorContentRefEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_VIEW_BASE_INFO_MONITOR_CONTENT_REF_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorViewBaseInfoSingle(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = indicatorViewBaseInfoSingleService.lambdaQuery()
        .eq(IndicatorViewBaseInfoSingleEntity::getAppId, appId)
        .in(IndicatorViewBaseInfoSingleEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list();
    if (!indicatorViewBaseInfoSingleEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_VIEW_BASE_INFO_MONITOR_CONTENT_REF_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorViewMonitorFollowupContentRef(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorViewMonitorFollowupContentRefEntity> indicatorViewMonitorFollowupContentRefEntityList = indicatorViewMonitorFollowupContentRefService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
        .in(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list();
    if (!indicatorViewMonitorFollowupContentRefEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_VIEW_MONITOR_CONTENT_REF_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorViewPhysicalExamEntity(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList = indicatorViewPhysicalExamService.lambdaQuery()
        .eq(IndicatorViewPhysicalExamEntity::getAppId, appId)
        .in(IndicatorViewPhysicalExamEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list();
    if (!indicatorViewPhysicalExamEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_VIEW_PHYSICAL_EXAM_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorViewSupportExamEntity(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList = indicatorViewSupportExamService.lambdaQuery()
        .eq(IndicatorViewSupportExamEntity::getAppId, appId)
        .in(IndicatorViewSupportExamEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list();
    if (!indicatorViewSupportExamEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_VIEW_SUPPORT_EXAM_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorExpressionEntity(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    List<IndicatorExpressionEntity> indicatorExpressionEntityList = indicatorExpressionService.lambdaQuery()
        .eq(IndicatorExpressionEntity::getAppId, appId)
        .in(IndicatorExpressionEntity::getPrincipalId, indicatorInstanceIdSet)
        .list();
    if (!indicatorExpressionEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_EXPRESSION_ENTITY);
    }
  }

  public void checkIndicatorInstanceDeleteInIndicatorExpressionInfluenceEntity(String appId, Set<String> indicatorInstanceIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}
    Set<String> dbExistIndicatorInstanceIdSet = new HashSet<>();
    indicatorExpressionInfluenceService.lambdaQuery()
        .eq(IndicatorExpressionInfluenceEntity::getAppId, appId)
        .list()
        .forEach(indicatorExpressionInfluenceEntity -> {
          String influenceIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
          List<String> influenceIndicatorInstanceIdSplitList = rsUtilBiz.getSplitList(influenceIndicatorInstanceIdList);
          String influencedIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluencedIndicatorInstanceIdList();
          List<String> influencedIndicatorInstanceIdSplitList = rsUtilBiz.getSplitList(influencedIndicatorInstanceIdList);
          dbExistIndicatorInstanceIdSet.addAll(influenceIndicatorInstanceIdSplitList);
          dbExistIndicatorInstanceIdSet.addAll(influencedIndicatorInstanceIdSplitList);
        });
    if (indicatorInstanceIdSet.stream().anyMatch(dbExistIndicatorInstanceIdSet::contains)) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_INSTANCE_FAILED_INDICATOR_EXPRESSION_INFLUENCE_ENTITY);
    }
  }



  public void checkIndicatorInstanceDelete(String appId, Set<String> indicatorInstanceIdSet) throws ExecutionException, InterruptedException {
  if (StringUtils.isBlank(appId) || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {return;}

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorViewBaseInfoDescrRef = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorViewBaseInfoDescrRef(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorViewBaseInfoDescrRef.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorViewBaseInfoMonitorContentRef = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorViewBaseInfoMonitorContentRef(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorViewBaseInfoMonitorContentRef.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorViewBaseInfoSingle = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorViewBaseInfoSingle(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorViewBaseInfoSingle.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorViewMonitorFollowupContentRef = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorViewMonitorFollowupContentRef(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorViewMonitorFollowupContentRef.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorViewPhysicalExamEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorViewPhysicalExamEntity(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorViewPhysicalExamEntity.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorViewSupportExamEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorViewSupportExamEntity(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorViewSupportExamEntity.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorExpressionEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorExpressionEntity(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorExpressionEntity.get();

    CompletableFuture<Void> cfCheckIndicatorInstanceDeleteInIndicatorExpressionInfluenceEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorInstanceDeleteInIndicatorExpressionInfluenceEntity(appId, indicatorInstanceIdSet);
    });
    cfCheckIndicatorInstanceDeleteInIndicatorExpressionInfluenceEntity.get();
  }

  public void checkIndicatorCategoryId(
      AtomicReference<IndicatorCategoryEntity> indicatorCategoryEntityAR,
      String indicatorCategoryId) {
    if (Objects.isNull(indicatorCategoryEntityAR) || StringUtils.isBlank(indicatorCategoryId)) {return;}
    IndicatorCategoryEntity indicatorCategoryEntity = indicatorCategoryService.lambdaQuery()
        .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
        .oneOpt()
        .orElseThrow(() -> {
          throw new IndicatorCategoryException(EnumESC.INDICATOR_CATEGORY_ID_IS_ILLEGAL);
        });
  }

  public void checkIndicatorCategoryDeleteInIndicatorViewMonitorFollowupEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<IndicatorViewMonitorFollowupEntity> indicatorViewMonitorFollowupEntityList = indicatorViewMonitorFollowupService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupEntity::getAppId, appId)
        .in(IndicatorViewMonitorFollowupEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
        .list();
    if (!indicatorViewMonitorFollowupEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_INDICATOR_VIEW_MONITOR_FOLLOWUP_ENTITY);
    }
  }

  public void checkIndicatorCategoryDeleteInIndicatorViewPhysicalExamEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList = indicatorViewPhysicalExamService.lambdaQuery()
        .eq(IndicatorViewPhysicalExamEntity::getAppId, appId)
        .in(IndicatorViewPhysicalExamEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
        .list();
    if (!indicatorViewPhysicalExamEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_INDICATOR_VIEW_PHYSICAL_EXAM_ENTITY);
    }
  }

  public void checkIndicatorCategoryDeleteInIndicatorViewSupportExamEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList = indicatorViewSupportExamService.lambdaQuery()
        .eq(IndicatorViewSupportExamEntity::getAppId, appId)
        .in(IndicatorViewSupportExamEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
        .list();
    if (!indicatorViewSupportExamEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_INDICATOR_VIEW_SUPPORT_EXAM_ENTITY);
    }
  }

  public void checkIndicatorCategoryDeleteInIndicatorJudgeRiskFactorEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityList = indicatorJudgeRiskFactorService.lambdaQuery()
        .eq(IndicatorJudgeRiskFactorEntity::getAppId, appId)
        .in(IndicatorJudgeRiskFactorEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
        .list();
    if (!indicatorJudgeRiskFactorEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_INDICATOR_JUDGE_RISK_FACTOR_ENTITY);
    }
  }

  public void checkIndicatorCategoryDeleteInIndicatorJudgeHealthGuidanceEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList = indicatorJudgeHealthGuidanceService.lambdaQuery()
        .eq(IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
        .in(IndicatorJudgeHealthGuidanceEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
        .list();
    if (!indicatorJudgeHealthGuidanceEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_INDICATOR_JUDGE_HEALTH_GUIDANCE_ENTITY);
    }
  }

  public void checkIndicatorCategoryDeleteInIndicatorJudgeHealthProblemEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList = indicatorJudgeHealthProblemService.lambdaQuery()
        .eq(IndicatorJudgeHealthProblemEntity::getAppId, appId)
        .in(IndicatorJudgeHealthProblemEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
        .list();
    if (!indicatorJudgeHealthProblemEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_INDICATOR_JUDGE_HEALTH_PROBLEM_ENTITY);
    }
  }

  public void checkIndicatorCategoryDeleteInTagInstanceEntity(String appId, Set<String> indicatorCategoryIdSet) {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}
    List<TagsInstanceEntity> tagsInstanceEntityList = tagsInstanceService.lambdaQuery()
        .eq(TagsInstanceEntity::getAppId, appId)
        .in(TagsInstanceEntity::getTagsCategoryId, indicatorCategoryIdSet)
        .list();
    if (!tagsInstanceEntityList.isEmpty()) {
      throw new RsIndicatorInstanceBizException(EnumESC.DELETE_INDICATOR_CATEGORY_FAILED_TAG_INSTANCE_ENTITY);
    }
  }

  public void checkIndicatorCategoryDelete(String appId, Set<String> indicatorCategoryIdSet) throws ExecutionException, InterruptedException {
    if (StringUtils.isBlank(appId) || Objects.isNull(indicatorCategoryIdSet) || indicatorCategoryIdSet.isEmpty()) {return;}

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInIndicatorViewMonitorFollowupEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInIndicatorViewMonitorFollowupEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInIndicatorViewMonitorFollowupEntity.get();

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInIndicatorViewPhysicalExamEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInIndicatorViewPhysicalExamEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInIndicatorViewPhysicalExamEntity.get();

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInIndicatorViewSupportExamEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInIndicatorViewSupportExamEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInIndicatorViewSupportExamEntity.get();

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInIndicatorJudgeRiskFactorEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInIndicatorJudgeRiskFactorEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInIndicatorJudgeRiskFactorEntity.get();

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInIndicatorJudgeHealthGuidanceEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInIndicatorJudgeHealthGuidanceEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInIndicatorJudgeHealthGuidanceEntity.get();

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInIndicatorJudgeHealthProblemEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInIndicatorJudgeHealthProblemEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInIndicatorJudgeHealthProblemEntity.get();

    CompletableFuture<Void> cfCheckIndicatorCategoryDeleteInTagInstanceEntity = CompletableFuture.runAsync(() -> {
      this.checkIndicatorCategoryDeleteInTagInstanceEntity(appId, indicatorCategoryIdSet);
    });
    cfCheckIndicatorCategoryDeleteInTagInstanceEntity.get();
  }

  public void populateIndicatorInstanceIdSet(
      Set<String> indicatorInstanceIdSet,
      String indicatorCategoryId) {
    if (Objects.isNull(indicatorInstanceIdSet) || StringUtils.isBlank(indicatorCategoryId)) {return;}
    indicatorInstanceService.lambdaQuery()
        .eq(IndicatorInstanceEntity::getIndicatorCategoryId, indicatorCategoryId)
        .list()
        .forEach(indicatorInstanceEntity -> {
          indicatorInstanceIdSet.add(indicatorInstanceEntity.getIndicatorInstanceId());
        });
  }

  public void populateKIndicatorInstanceIdVIndicatorInstanceEntityMap(
      Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceEntityMap,
      String appId) {
    if (Objects.isNull(kIndicatorInstanceIdVIndicatorInstanceEntityMap) || StringUtils.isBlank(appId)) {return;}
    indicatorInstanceService.lambdaQuery()
        .eq(IndicatorInstanceEntity::getAppId, appId)
        .list()
        .forEach(indicatorInstanceEntity -> {
          kIndicatorInstanceIdVIndicatorInstanceEntityMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity);
        });
  }

  public void populateKIndicatorInstanceIdVIndicatorRuleEntityMap(
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap,
      Set<String> indicatorInstanceIdSet) {
    if (Objects.isNull(kIndicatorInstanceIdVIndicatorRuleEntityMap)
        || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()
    ) {return;}
    indicatorRuleService.lambdaQuery()
        .in(IndicatorRuleEntity::getVariableId, indicatorInstanceIdSet)
        .list()
        .forEach(indicatorRuleEntity -> {
          kIndicatorInstanceIdVIndicatorRuleEntityMap.put(indicatorRuleEntity.getVariableId(), indicatorRuleEntity);
        });
  }

  public void populateHealthPointIndicatorRuleEntity(
      AtomicReference<IndicatorRuleEntity> indicatorRuleEntityAR,
      String appId) {
    if (Objects.isNull(indicatorRuleEntityAR) || StringUtils.isBlank(appId)) {return;}
    IndicatorInstanceEntity indicatorInstanceEntity = indicatorInstanceService.lambdaQuery()
        .eq(IndicatorInstanceEntity::getAppId, appId)
        .eq(IndicatorInstanceEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
        .one();
    if (Objects.isNull(indicatorInstanceEntity)) {return;}
    indicatorRuleService.lambdaQuery()
        .eq(IndicatorRuleEntity::getVariableId, indicatorInstanceEntity.getIndicatorInstanceId())
        .oneOpt()
        .ifPresent(indicatorRuleEntityAR::set);
  }
}
