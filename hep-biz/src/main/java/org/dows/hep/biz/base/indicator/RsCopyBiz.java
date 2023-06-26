package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.RsCopyExperimentRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyPersonIndicatorRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.RsCopyException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCopyBiz {
  private final IdGenerator idGenerator;
  private final ExperimentOrgService experimentOrgService;
  private final ExperimentOrgModuleRsService experimentOrgModuleRsService;
  private final CaseInstanceService caseInstanceService;
  private final CaseOrgService caseOrgService;
  private final CaseOrgModuleService caseOrgModuleService;
  private final CaseOrgModuleFuncRefService caseOrgModuleFuncRefService;
  private final IndicatorFuncService indicatorFuncService;
  private final IndicatorInstanceService indicatorInstanceService;
  private final IndicatorCategoryService indicatorCategoryService;
  /* runsix:indicator */
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final CasePersonService casePersonService;
  private final ExperimentPersonService experimentPersonService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorRuleService caseIndicatorRuleService;
  /* runsix:view */
  private final IndicatorViewBaseInfoService indicatorViewBaseInfoService;
  private final ExperimentIndicatorViewBaseInfoRsService experimentIndicatorViewBaseInfoRsService;
  private final IndicatorViewBaseInfoDescrService indicatorViewBaseInfoDescrService;
  private final ExperimentIndicatorViewBaseInfoDescrRsService experimentIndicatorViewBaseInfoDescrRsService;
  private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;
  private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
  private final ExperimentIndicatorViewBaseInfoMonitorRsService experimentIndicatorViewBaseInfoMonitorRsService;
  private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
  private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;
  private final IndicatorViewBaseInfoSingleService indicatorViewBaseInfoSingleService;
  private final ExperimentIndicatorViewBaseInfoSingleRsService experimentIndicatorViewBaseInfoSingleRsService;
  private final IndicatorViewMonitorFollowupService indicatorViewMonitorFollowupService;
  private final IndicatorViewMonitorFollowupFollowupContentService indicatorViewMonitorFollowupFollowupContentService;
  private final IndicatorViewMonitorFollowupContentRefService indicatorViewMonitorFollowupContentRefService;
  private final IndicatorViewPhysicalExamService indicatorViewPhysicalExamService;
  private final ExperimentIndicatorViewPhysicalExamRsService experimentIndicatorViewPhysicalExamRsService;
  private final IndicatorViewSupportExamService indicatorViewSupportExamService;
  private final ExperimentIndicatorViewSupportExamRsService experimentIndicatorViewSupportExamRsService;
  /* runsix:todo wuzhilin */
  /* runsix:judge */
  private final IndicatorJudgeRiskFactorService indicatorJudgeRiskFactorService;
  private final ExperimentIndicatorJudgeRiskFactorRsService experimentIndicatorJudgeRiskFactorRsService;
  private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;
  private final ExperimentIndicatorJudgeHealthGuidanceRsService experimentIndicatorJudgeHealthGuidanceRsService;
  private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;
  private final ExperimentIndicatorJudgeHealthProblemRsService experimentIndicatorJudgeHealthProblemRsService;
  private final IndicatorJudgeDiseaseProblemService indicatorJudgeDiseaseProblemService;
  private final ExperimentIndicatorJudgeDiseaseProblemRsService experimentIndicatorJudgeDiseaseProblemRsService;
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyIndicatorFunc(RsCopyExperimentRequestRs rsCopyExperimentRequestRs) {
    List<ExperimentOrgModuleRsEntity> experimentOrgModuleRsEntityList = new ArrayList<>();
    List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewPhysicalExamRsEntity> experimentIndicatorViewPhysicalExamRsEntityList = new ArrayList<>();
    List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewSupportExamRsEntity> experimentIndicatorViewSupportExamRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorJudgeRiskFactorRsEntity> experimentIndicatorJudgeRiskFactorRsEntityList = new ArrayList<>();
    List<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityList = new ArrayList<>();
    List<ExperimentIndicatorJudgeHealthGuidanceRsEntity> experimentIndicatorJudgeHealthGuidanceRsEntityList = new ArrayList<>();
    List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList = new ArrayList<>();
    List<ExperimentIndicatorJudgeHealthProblemRsEntity> experimentIndicatorJudgeHealthProblemRsEntityList = new ArrayList<>();
    List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList = new ArrayList<>();
    List<ExperimentIndicatorJudgeDiseaseProblemRsEntity> experimentIndicatorJudgeDiseaseProblemRsEntityList = new ArrayList<>();
    List<IndicatorJudgeDiseaseProblemEntity> indicatorJudgeDiseaseProblemEntityList = new ArrayList<>();
    String appId = rsCopyExperimentRequestRs.getAppId();
    String caseInstanceId = rsCopyExperimentRequestRs.getCaseInstanceId();
    String experimentInstanceId = rsCopyExperimentRequestRs.getExperimentInstanceId();
    Map<String, ExperimentOrgEntity> kExperimentOrgIdVExperimentOrgEntityMap = new HashMap<>();
    Map<String, List<ExperimentOrgEntity>> kExperimentIdVExperimentOrgEntityListMap = new HashMap<>();
    Map<String, List<ExperimentOrgEntity>> kCaseOrgIdVExperimentOrgEntityListMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorFuncIdVIndicatorFuncIdMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorFuncIdVIndicatorCategoryIdMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorFuncIdVIndicatorFuncNameMap = new HashMap<>();
    caseInstanceService.lambdaQuery()
        .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method RsCopyBiz.rsCopyViewIndicator param caseInstanceId:{} is illegal", caseInstanceId);
          throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
        });
    Set<String> caseOrgIdSet = new HashSet<>();
    List<CaseOrgEntity> caseOrgEntityList = caseOrgService.lambdaQuery()
        .eq(CaseOrgEntity::getAppId, appId)
        .eq(CaseOrgEntity::getCaseInstanceId, caseInstanceId)
        .list()
        .stream()
        .peek(caseOrgEntity -> caseOrgIdSet.add(caseOrgEntity.getCaseOrgId()))
        .collect(Collectors.toList());
    if (caseOrgIdSet.isEmpty()) {
      log.warn("method RsCopyBiz.rsCopyViewIndicator has no caseOrg");
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    Map<String, CaseOrgModuleEntity> kCaseOrgModuleIdVCaseOrgModuleEntityMap = new HashMap<>();
    Map<String, List<CaseOrgModuleEntity>> kCaseOrgIdVCaseOrgModuleEntityListMap = new HashMap<>();
    caseOrgModuleService.lambdaQuery()
        .eq(CaseOrgModuleEntity::getAppId, appId)
        .in(CaseOrgModuleEntity::getCaseOrgId, caseOrgIdSet)
        .list()
        .forEach(caseOrgModuleEntity -> {
          List<CaseOrgModuleEntity> caseOrgModuleEntityList1 = kCaseOrgIdVCaseOrgModuleEntityListMap.get(caseOrgModuleEntity.getCaseOrgId());
          if (Objects.isNull(caseOrgModuleEntityList1)) {
            caseOrgModuleEntityList1 = new ArrayList<>();
          }
          caseOrgModuleEntityList1.add(caseOrgModuleEntity);
          kCaseOrgIdVCaseOrgModuleEntityListMap.put(caseOrgModuleEntity.getCaseOrgId(), caseOrgModuleEntityList1);
          kCaseOrgModuleIdVCaseOrgModuleEntityMap.put(caseOrgModuleEntity.getCaseOrgModuleId(), caseOrgModuleEntity);
        });
    Set<String> caseOrgModuleIdSet = kCaseOrgModuleIdVCaseOrgModuleEntityMap.keySet();
    if (caseOrgModuleIdSet.isEmpty()) {
      log.warn("method RsCopyBiz.rsCopyViewIndicator has no caseOrgModule");
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    Set<String> indicatorFuncIdSet = new HashSet<>();
    Map<String, Map<String, Integer>> kIndicatorFuncIdVKCaseOrgModuleIdVSeqMap = new HashMap<>();
    Map<String, List<String>> kIndicatorFuncIdVCaseOrgModuleIdList = new HashMap<>();
    caseOrgModuleFuncRefService.lambdaQuery()
        .eq(CaseOrgModuleFuncRefEntity::getAppId, appId)
        .in(CaseOrgModuleFuncRefEntity::getCaseOrgModuleId, caseOrgModuleIdSet)
        .list()
        .forEach(caseOrgModuleFuncRefEntity -> {
          String indicatorFuncId = caseOrgModuleFuncRefEntity.getIndicatorFuncId();
          indicatorFuncIdSet.add(indicatorFuncId);
          List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
          if (Objects.isNull(caseOrgModuleIdList)) {
            caseOrgModuleIdList = new ArrayList<>();
          }
          caseOrgModuleIdList.add(caseOrgModuleFuncRefEntity.getCaseOrgModuleId());
          kIndicatorFuncIdVCaseOrgModuleIdList.put(indicatorFuncId, caseOrgModuleIdList);
          Map<String, Integer> aMap = kIndicatorFuncIdVKCaseOrgModuleIdVSeqMap.get(indicatorFuncId);
          if (Objects.isNull(aMap)) {
            aMap = new HashMap<>();
          }
          aMap.put(caseOrgModuleFuncRefEntity.getCaseOrgModuleId(), caseOrgModuleFuncRefEntity.getSeq());
          kIndicatorFuncIdVKCaseOrgModuleIdVSeqMap.put(indicatorFuncId, aMap);
        });
    if (indicatorFuncIdSet.isEmpty()) {
      log.warn("method RsCopyBiz.rsCopyViewIndicator has no indicatorFunc");
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    experimentOrgService.lambdaQuery()
      .eq(ExperimentOrgEntity::getAppId, appId)
      .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
      .list()
      .forEach(experimentOrgEntity -> {
        String caseOrgId = experimentOrgEntity.getCaseOrgId();
        List<ExperimentOrgEntity> experimentOrgEntityList1 = kCaseOrgIdVExperimentOrgEntityListMap.get(caseOrgId);
        if (Objects.isNull(experimentOrgEntityList1)) {
          experimentOrgEntityList1 = new ArrayList<>();
        }
        experimentOrgEntityList1.add(experimentOrgEntity);
        kCaseOrgIdVExperimentOrgEntityListMap.put(caseOrgId, experimentOrgEntityList1);
        String experimentOrgId = experimentOrgEntity.getExperimentOrgId();
        kExperimentOrgIdVExperimentOrgEntityMap.put(experimentOrgId, experimentOrgEntity);
        List<ExperimentOrgEntity> experimentOrgEntityList0 = kExperimentIdVExperimentOrgEntityListMap.get(experimentInstanceId);
        if (Objects.isNull(experimentOrgEntityList0)) {
          experimentOrgEntityList0 = new ArrayList<>();
        }
        experimentOrgEntityList0.add(experimentOrgEntity);
        kExperimentIdVExperimentOrgEntityListMap.put(experimentInstanceId, experimentOrgEntityList0);
      });
    Map<String, List<String>> kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap = new HashMap<>();
    Map<String, IndicatorFuncEntity> kIndicatorFuncIdVIndicatorFuncEntityMap = new HashMap<>();
    /* runsix: view*/
    Set<String> funcIndicatorViewBaseInfoIdSet = new HashSet<>();
    Set<String> funcIndicatorViewMonitorFollowupIdSet = new HashSet<>();
    Set<String> funcIndicatorViewPhysicalExamIdSet = new HashSet<>();
    Set<String> funcIndicatorViewSupportExamIdSet = new HashSet<>();
    /* runsix:operate TODO wuzhilin */
    /* runsix:judge */
    Set<String> funcIndicatorJudgeRiskFactorIdSet = new HashSet<>();
    Set<String> funcIndicatorJudgeHealthProblemIdSet = new HashSet<>();
    Set<String> funcIndicatorJudgeHealthGuidanceIdSet = new HashSet<>();
    Set<String> funcIndicatorJudgeDiseaseProblemIdSet = new HashSet<>();
    indicatorFuncService.lambdaQuery()
        .eq(IndicatorFuncEntity::getAppId, appId)
        .in(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncIdSet)
        .list()
        .forEach(indicatorFuncEntity -> {
          kIndicatorFuncIdVIndicatorFuncEntityMap.put(indicatorFuncEntity.getIndicatorFuncId(), indicatorFuncEntity);
          String indicatorFuncId = indicatorFuncEntity.getIndicatorFuncId();
          String indicatorCategoryId = indicatorFuncEntity.getIndicatorCategoryId();
          /* runsix:TODO wuzhilin那边剩余的 */
          switch (EnumIndicatorCategory.getByCode(indicatorCategoryId)) {
            case VIEW_MANAGEMENT_BASE_INFO:
              funcIndicatorViewBaseInfoIdSet.add(indicatorFuncId);
              break;
            case VIEW_MANAGEMENT_MONITOR_FOLLOWUP:
              funcIndicatorViewMonitorFollowupIdSet.add(indicatorFuncId);
              break;
            case VIEW_MANAGEMENT_NO_REPORT_TWO_LEVEL:
              funcIndicatorViewPhysicalExamIdSet.add(indicatorFuncId);
              break;
            case VIEW_MANAGEMENT_NO_REPORT_FOUR_LEVEL:
              funcIndicatorViewSupportExamIdSet.add(indicatorFuncId);
              break;
            case JUDGE_MANAGEMENT_RISK_FACTOR:
              funcIndicatorJudgeRiskFactorIdSet.add(indicatorFuncId);
              break;
            case JUDGE_MANAGEMENT_HEALTH_PROBLEM:
              funcIndicatorJudgeHealthProblemIdSet.add(indicatorFuncId);
              break;
            case JUDGE_MANAGEMENT_HEALTH_GUIDANCE:
              funcIndicatorJudgeHealthGuidanceIdSet.add(indicatorFuncId);
              break;
            case JUDGE_MANAGEMENT_DISEASE_PROBLEM:
              funcIndicatorJudgeDiseaseProblemIdSet.add(indicatorFuncId);
              break;
            default:
              log.error("RsCopyBiz.rsCopyViewIndicator indicatorCategoryId:{} is illegal", indicatorCategoryId);
          }
        });
    Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceEntityMap = new HashMap<>();
    indicatorInstanceService.lambdaQuery()
        .eq(IndicatorInstanceEntity::getAppId, appId)
        .list()
        .forEach(indicatorInstanceEntity -> {
          kIndicatorInstanceIdVIndicatorInstanceEntityMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity);
        });
    Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdVIndicatorCategoryEntityMap = new HashMap<>();
    Map<String, String> kIndicatorCategoryIdVParentIndicatorCategoryIdMap = new HashMap<>();
    indicatorCategoryService.lambdaQuery()
        .eq(IndicatorCategoryEntity::getAppId, appId)
        .list()
        .forEach(indicatorCategoryEntity -> {
          kIndicatorCategoryIdVIndicatorCategoryEntityMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity);
          kIndicatorCategoryIdVParentIndicatorCategoryIdMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity.getPid());
        });
    /* runsix:查看指标-基本信息 */
    /* runsix:查看指标-基本信息-指标描述表 */
    List<ExperimentIndicatorViewBaseInfoRsEntity> experimentIndicatorViewBaseInfoRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoDescRsEntity> experimentIndicatorViewBaseInfoDescRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoMonitorRsEntity>  experimentIndicatorViewBaseInfoMonitorRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoSingleRsEntity> experimentIndicatorViewBaseInfoSingleRsEntityList = new ArrayList<>();
    Map<String, IndicatorViewBaseInfoEntity> kBaseInfoIdVBaseInfoMap = new HashMap<>();
    Set<String> baseInfoIdSet = new HashSet<>();
    Map<String, List<IndicatorViewBaseInfoDescrEntity>> kBaseInfoIdVIndicatorViewBaseInfoDescrEntityListMap = new HashMap<>();
    Map<String, List<IndicatorViewBaseInfoMonitorEntity>> kBaseInfoIdVIndicatorViewBaseInfoMonitorEntityListMap = new HashMap<>();
    Map<String, List<IndicatorViewBaseInfoSingleEntity>> kBaseInfoIdVIndicatorViewBaseInfoSingleEntityListMap = new HashMap<>();
    Set<String> baseInfoDescrIdSet = new HashSet<>();
    Map<String, List<IndicatorViewBaseInfoDescrRefEntity>> kBaseInfoDescrIdVBaseInfoDescrRefListMap = new HashMap<>();
    Set<String> iVBIMIdSet = new HashSet<>();
    Map<String, List<IndicatorViewBaseInfoMonitorContentEntity>> kIVBIMIdVIVBIMContentEntityListMap = new HashMap<>();
    Set<String> iVBIMContentIdSet = new HashSet<>();
    Map<String, List<IndicatorViewBaseInfoMonitorContentRefEntity>> kIVBIMContentIdVIVBIMContentRefEntityListMap = new HashMap<>();
    if (!funcIndicatorViewBaseInfoIdSet.isEmpty()) {
      indicatorViewBaseInfoService.lambdaQuery()
          .eq(IndicatorViewBaseInfoEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoEntity::getIndicatorFuncId, funcIndicatorViewBaseInfoIdSet)
          .list()
          .forEach(indicatorViewBaseInfoEntity -> {
            baseInfoIdSet.add(indicatorViewBaseInfoEntity.getIndicatorViewBaseInfoId());
            kBaseInfoIdVBaseInfoMap.put(indicatorViewBaseInfoEntity.getIndicatorViewBaseInfoId(), indicatorViewBaseInfoEntity);
          });
    }
    if (!baseInfoIdSet.isEmpty()) {
      indicatorViewBaseInfoDescrService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoId, baseInfoIdSet)
          .list()
          .forEach(indicatorViewBaseInfoDescrEntity -> {
            String indicatorViewBaseInfoId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoId();
            List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = kBaseInfoIdVIndicatorViewBaseInfoDescrEntityListMap.get(indicatorViewBaseInfoId);
            if (Objects.isNull(indicatorViewBaseInfoDescrEntityList)) {
              indicatorViewBaseInfoDescrEntityList = new ArrayList<>();
            }
            indicatorViewBaseInfoDescrEntityList.add(indicatorViewBaseInfoDescrEntity);
            kBaseInfoIdVIndicatorViewBaseInfoDescrEntityListMap.put(indicatorViewBaseInfoId, indicatorViewBaseInfoDescrEntityList);
            baseInfoDescrIdSet.add(indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId());
          });
      indicatorViewBaseInfoMonitorService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoId, baseInfoIdSet)
          .list()
          .forEach(indicatorViewBaseInfoMonitorEntity -> {
            String indicatorViewBaseInfoId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId();
            List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList = kBaseInfoIdVIndicatorViewBaseInfoMonitorEntityListMap.get(indicatorViewBaseInfoId);
            if (Objects.isNull(indicatorViewBaseInfoMonitorEntityList)) {
              indicatorViewBaseInfoMonitorEntityList = new ArrayList<>();
            }
            indicatorViewBaseInfoMonitorEntityList.add(indicatorViewBaseInfoMonitorEntity);
            kBaseInfoIdVIndicatorViewBaseInfoMonitorEntityListMap.put(indicatorViewBaseInfoId, indicatorViewBaseInfoMonitorEntityList);
            iVBIMIdSet.add(indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoMonitorId());
          });
      indicatorViewBaseInfoSingleService.lambdaQuery()
          .eq(IndicatorViewBaseInfoSingleEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoId, baseInfoIdSet)
          .list()
          .forEach(indicatorViewBaseInfoSingleEntity -> {
            String indicatorViewBaseInfoId = indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoId();
            List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = kBaseInfoIdVIndicatorViewBaseInfoSingleEntityListMap.get(indicatorViewBaseInfoId);
            if (Objects.isNull(indicatorViewBaseInfoSingleEntityList)) {
              indicatorViewBaseInfoSingleEntityList = new ArrayList<>();
            }
            indicatorViewBaseInfoSingleEntityList.add(indicatorViewBaseInfoSingleEntity);
            kBaseInfoIdVIndicatorViewBaseInfoSingleEntityListMap.put(indicatorViewBaseInfoId, indicatorViewBaseInfoSingleEntityList);
          });
    }
    if (!baseInfoDescrIdSet.isEmpty()) {
      indicatorViewBaseInfoDescrRefService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescId, baseInfoDescrIdSet)
          .list()
          .forEach(indicatorViewBaseInfoDescrRefEntity -> {
            String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId();
            List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = kBaseInfoDescrIdVBaseInfoDescrRefListMap.get(indicatorViewBaseInfoDescId);
            if (Objects.isNull(indicatorViewBaseInfoDescrRefEntityList)) {
              indicatorViewBaseInfoDescrRefEntityList = new ArrayList<>();
            }
            indicatorViewBaseInfoDescrRefEntityList.add(indicatorViewBaseInfoDescrRefEntity);
            kBaseInfoDescrIdVBaseInfoDescrRefListMap.put(indicatorViewBaseInfoDescId, indicatorViewBaseInfoDescrRefEntityList);
          });
      /* runsix:sort */
      kBaseInfoDescrIdVBaseInfoDescrRefListMap.forEach((baseInfoId, baseInfoDescrRefList) -> {
        baseInfoDescrRefList.sort(Comparator.comparingInt(IndicatorViewBaseInfoDescrRefEntity::getSeq));
      });
    }
    if (!iVBIMIdSet.isEmpty()) {
      indicatorViewBaseInfoMonitorContentService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorId, iVBIMIdSet)
          .list()
          .forEach(indicatorViewBaseInfoMonitorContentEntity -> {
            String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId();
            iVBIMContentIdSet.add(indicatorViewBaseInfoMonitorContentId);
            List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = kIVBIMIdVIVBIMContentEntityListMap.get(indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId());
            if (Objects.isNull(indicatorViewBaseInfoMonitorContentEntityList)) {
              indicatorViewBaseInfoMonitorContentEntityList = new ArrayList<>();
            }
            indicatorViewBaseInfoMonitorContentEntityList.add(indicatorViewBaseInfoMonitorContentEntity);
            kIVBIMIdVIVBIMContentEntityListMap.put(indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId(), indicatorViewBaseInfoMonitorContentEntityList);
          });
      /* runsix:sort */
      kIVBIMIdVIVBIMContentEntityListMap.forEach((iVBIMId, indicatorViewBaseInfoMonitorContentEntityList) -> {
        indicatorViewBaseInfoMonitorContentEntityList.sort(Comparator.comparingInt(IndicatorViewBaseInfoMonitorContentEntity::getSeq));
      });
    }
    if (!iVBIMContentIdSet.isEmpty()) {
      indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentId, iVBIMContentIdSet)
          .list()
          .forEach(indicatorViewBaseInfoMonitorContentRefEntity -> {
            String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId();
            List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = kIVBIMContentIdVIVBIMContentRefEntityListMap.get(indicatorViewBaseInfoMonitorContentId);
            if (Objects.isNull(indicatorViewBaseInfoMonitorContentRefEntityList)) {
              indicatorViewBaseInfoMonitorContentRefEntityList = new ArrayList<>();
            }
            indicatorViewBaseInfoMonitorContentRefEntityList.add(indicatorViewBaseInfoMonitorContentRefEntity);
            kIVBIMContentIdVIVBIMContentRefEntityListMap.put(indicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentRefEntityList);
          });
      /* runsix:sort */
      kIVBIMContentIdVIVBIMContentRefEntityListMap.forEach((indicatorViewBaseInfoMonitorContentEntityId, indicatorViewBaseInfoMonitorContentRefEntityList) -> {
        indicatorViewBaseInfoMonitorContentRefEntityList.sort(Comparator.comparingInt(IndicatorViewBaseInfoMonitorContentRefEntity::getSeq));
      });
    }
    kBaseInfoIdVBaseInfoMap.forEach((indicatorViewBaseInfoId, indicatorViewBaseInfoEntity) -> {
      String experimentIndicatorViewBaseInfoId = idGenerator.nextIdStr();
      String indicatorFuncId = indicatorViewBaseInfoEntity.getIndicatorFuncId();
      IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
      String name2 = indicatorFuncEntity.getName();
      kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(experimentIndicatorViewBaseInfoId, indicatorFuncId);
      List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
      if (Objects.nonNull(caseOrgModuleIdList)) {
        caseOrgModuleIdList.forEach(caseOrgModuleId -> {
          List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
          if (Objects.isNull(experimentIndicatorFuncIdList)) {
            experimentIndicatorFuncIdList = new ArrayList<>();
          }
          experimentIndicatorFuncIdList.add(experimentIndicatorViewBaseInfoId);
          kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
          kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(experimentIndicatorViewBaseInfoId, EnumIndicatorCategory.VIEW_MANAGEMENT_BASE_INFO.getCode());
          kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(experimentIndicatorViewBaseInfoId, name2);
        });
      }
      ExperimentIndicatorViewBaseInfoRsEntity experimentIndicatorViewBaseInfoRsEntity = ExperimentIndicatorViewBaseInfoRsEntity
          .builder()
          .experimentIndicatorViewBaseInfoId(experimentIndicatorViewBaseInfoId)
          .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
          .experimentId(experimentInstanceId)
          .caseId(caseInstanceId)
          .name(name2)
          .appId(appId)
          .build();
      experimentIndicatorViewBaseInfoRsEntityList.add(experimentIndicatorViewBaseInfoRsEntity);
      /* runsix:查看指标-基本信息-指标描述表 */
      List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = kBaseInfoIdVIndicatorViewBaseInfoDescrEntityListMap.get(indicatorViewBaseInfoId);
      if (Objects.nonNull(indicatorViewBaseInfoDescrEntityList) && !indicatorViewBaseInfoDescrEntityList.isEmpty()) {
        indicatorViewBaseInfoDescrEntityList.forEach(indicatorViewBaseInfoDescrEntity -> {
          String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId();
          String name = indicatorViewBaseInfoDescrEntity.getName();
          Integer seq = indicatorViewBaseInfoDescrEntity.getSeq();
          String indicatorInstanceIdArray = null;
          List<String> indicatorInstanceIdList = new ArrayList<>();
          String indicatorInstanceNameArray = null;
          List<String> indicatorInstanceNameList = new ArrayList<>();
          List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = kBaseInfoDescrIdVBaseInfoDescrRefListMap.get(indicatorViewBaseInfoDescId);
          if (Objects.nonNull(indicatorViewBaseInfoDescrRefEntityList) && !indicatorViewBaseInfoDescrRefEntityList.isEmpty()) {
            indicatorViewBaseInfoDescrRefEntityList.forEach(indicatorViewBaseInfoDescrRefEntity -> {
              String indicatorInstanceId = indicatorViewBaseInfoDescrRefEntity.getIndicatorInstanceId();
              IndicatorInstanceEntity indicatorInstanceEntity = kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorInstanceId);
              if (Objects.nonNull(indicatorInstanceEntity)) {
                indicatorInstanceIdList.add(indicatorInstanceId);
                indicatorInstanceNameList.add(indicatorInstanceEntity.getIndicatorName());
              }
            });
          }
          if (!indicatorInstanceIdList.isEmpty()) {
            indicatorInstanceIdArray = String.join(EnumString.COMMA.getStr(), indicatorInstanceIdList);
          }
          if (!indicatorInstanceNameList.isEmpty()) {
            indicatorInstanceNameArray = String.join(EnumString.COMMA.getStr(), indicatorInstanceNameList);
          }
          ExperimentIndicatorViewBaseInfoDescRsEntity experimentIndicatorViewBaseInfoDescRsEntity = ExperimentIndicatorViewBaseInfoDescRsEntity
              .builder()
              .experimentIndicatorViewBaseInfoDescId(idGenerator.nextIdStr())
              .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescId)
              .experimentId(experimentInstanceId)
              .caseId(caseInstanceId)
              .appId(appId)
              .indicatorViewBaseInfoId(experimentIndicatorViewBaseInfoId)
              .name(name)
              .seq(seq)
              .indicatorInstanceIdArray(indicatorInstanceIdArray)
              .build();
          experimentIndicatorViewBaseInfoDescRsEntityList.add(experimentIndicatorViewBaseInfoDescRsEntity);
        });
      }
      /* runsix:查看指标-基本信息-指标描述表 */
      /* runsix:查看指标-基本信息-指标监测表 */
      List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList = kBaseInfoIdVIndicatorViewBaseInfoMonitorEntityListMap.get(indicatorViewBaseInfoId);
      if (Objects.nonNull(indicatorViewBaseInfoMonitorEntityList) && !indicatorViewBaseInfoMonitorEntityList.isEmpty()) {
        indicatorViewBaseInfoMonitorEntityList.forEach(indicatorViewBaseInfoMonitorEntity -> {
          String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoMonitorId();
          String name = indicatorViewBaseInfoMonitorEntity.getName();
          Integer seq = indicatorViewBaseInfoMonitorEntity.getSeq();
          String ivbimContentNameArray = null;
          List<String> ivbimContentNameList = new ArrayList<>();
          String ivbimContentRefIndicatorInstanceIdArray = null;
          List<String> ivbimContentRefIndicatorInstanceIdList = new ArrayList<>();
          String ivbimContentRefIndicatorInstanceNameArray = null;
          List<String> ivbimContentRefIndicatorInstanceNameList = new ArrayList<>();
          List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = kIVBIMIdVIVBIMContentEntityListMap.get(indicatorViewBaseInfoMonitorId);
          if (Objects.nonNull(indicatorViewBaseInfoMonitorContentEntityList) && !indicatorViewBaseInfoMonitorContentEntityList.isEmpty()) {
            indicatorViewBaseInfoMonitorContentEntityList.forEach(indicatorViewBaseInfoMonitorContentEntity -> {
              String name1 = indicatorViewBaseInfoMonitorContentEntity.getName();
              ivbimContentNameList.add(name1);
              String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId();
              List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = kIVBIMContentIdVIVBIMContentRefEntityListMap.get(indicatorViewBaseInfoMonitorContentId);
              if (Objects.nonNull(indicatorViewBaseInfoMonitorContentRefEntityList) && !indicatorViewBaseInfoMonitorContentRefEntityList.isEmpty()) {
                String singelIvbimContentRefIndicatorInstanceNameArray = null;
                List<String> singleIvbimContentRefIndicatorInstanceNameList = new ArrayList<>();
                String singleIvbimContentRefIndicatorInstanceIdArray = null;
                List<String> signleIvbimContentRefIndicatorInstanceIdList = new ArrayList<>();
                indicatorViewBaseInfoMonitorContentRefEntityList.forEach(indicatorViewBaseInfoMonitorContentRefEntity -> {
                  String indicatorInstanceId = indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorInstanceId();
                  IndicatorInstanceEntity indicatorInstanceEntity = kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorInstanceId);
                  if (Objects.nonNull(indicatorInstanceEntity)) {
                    signleIvbimContentRefIndicatorInstanceIdList.add(indicatorInstanceId);
                    singleIvbimContentRefIndicatorInstanceNameList.add(indicatorInstanceEntity.getIndicatorName());
                  }
                });
                singleIvbimContentRefIndicatorInstanceIdArray = String.join(EnumString.COMMA.getStr(), signleIvbimContentRefIndicatorInstanceIdList);
                ivbimContentRefIndicatorInstanceIdList.add(singleIvbimContentRefIndicatorInstanceIdArray);
                singelIvbimContentRefIndicatorInstanceNameArray = String.join(EnumString.COMMA.getStr(), singleIvbimContentRefIndicatorInstanceNameList);
                ivbimContentRefIndicatorInstanceNameList.add(singelIvbimContentRefIndicatorInstanceNameArray);
              }

            });
            ivbimContentNameArray = String.join(EnumString.COMMA.getStr(), ivbimContentNameList);
            ivbimContentRefIndicatorInstanceIdArray = String.join(EnumString.JIN.getStr(), ivbimContentRefIndicatorInstanceIdList);
            ivbimContentRefIndicatorInstanceNameArray = String.join(EnumString.JIN.getStr(), ivbimContentRefIndicatorInstanceNameList);

          }
          ExperimentIndicatorViewBaseInfoMonitorRsEntity experimentIndicatorViewBaseInfoMonitorRsEntity = ExperimentIndicatorViewBaseInfoMonitorRsEntity
              .builder()
              .experimentIndicatorViewBaseInfoMonitorId(idGenerator.nextIdStr())
              .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorId)
              .experimentId(experimentInstanceId)
              .caseId(caseInstanceId)
              .indicatorViewBaseInfoId(experimentIndicatorViewBaseInfoId)
              .appId(appId)
              .name(name)
              .seq(seq)
              .ivbimContentNameArray(ivbimContentNameArray)
              .ivbimContentRefIndicatorInstanceIdArray(ivbimContentRefIndicatorInstanceIdArray)
              .build();
          experimentIndicatorViewBaseInfoMonitorRsEntityList.add(experimentIndicatorViewBaseInfoMonitorRsEntity);
        });
      }
      /* runsix:查看指标-基本信息-指标监测表 */
      /* runsix:查看指标-基本信息-单一指标表 */
      List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = kBaseInfoIdVIndicatorViewBaseInfoSingleEntityListMap.get(indicatorViewBaseInfoId);
      if (Objects.nonNull(indicatorViewBaseInfoSingleEntityList) && !indicatorViewBaseInfoSingleEntityList.isEmpty()) {
        indicatorViewBaseInfoSingleEntityList.forEach(indicatorViewBaseInfoSingleEntity -> {
          String indicatorInstanceId = null;
          IndicatorInstanceEntity indicatorInstanceEntity = kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorViewBaseInfoSingleEntity.getIndicatorInstanceId());
          if (Objects.nonNull(indicatorInstanceEntity)) {
            indicatorInstanceId = indicatorInstanceEntity.getIndicatorInstanceId();
          }
          ExperimentIndicatorViewBaseInfoSingleRsEntity experimentIndicatorViewBaseInfoSingleRsEntity = ExperimentIndicatorViewBaseInfoSingleRsEntity
              .builder()
              .experimentIndicatorViewBaseInfoSingleId(idGenerator.nextIdStr())
              .indicatorViewBaseInfoSingleId(indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoSingleId())
              .experimentId(experimentInstanceId)
              .caseId(caseInstanceId)
              .appId(appId)
              .indicatorViewBaseInfoId(experimentIndicatorViewBaseInfoId)
              .indicatorInstanceId(indicatorInstanceId)
              .seq(indicatorViewBaseInfoSingleEntity.getSeq())
              .build();
          experimentIndicatorViewBaseInfoSingleRsEntityList.add(experimentIndicatorViewBaseInfoSingleRsEntity);
        });
      }
      /* runsix:查看指标-基本信息-单一指标表 */
    });
    /* runsix:查看指标-基本信息 */
    /* runsix:TODO 监测随访 暂时使用姜霞的 */
    /* runsix:TODO 监测随访 暂时使用姜霞的 */
    /* runsix:查看指标-体格检查-二级类-无报告 */
    Map<String, String> kIndicatorFuncIdVPEExperimentIndicatorFuncIdMap = new HashMap<>();
    if (!funcIndicatorViewPhysicalExamIdSet.isEmpty()) {
      indicatorViewPhysicalExamService.lambdaQuery()
          .eq(IndicatorViewPhysicalExamEntity::getAppId, appId)
          .in(IndicatorViewPhysicalExamEntity::getIndicatorFuncId, funcIndicatorViewPhysicalExamIdSet)
          .list()
          .forEach(indicatorViewPhysicalExamEntity -> {
            indicatorViewPhysicalExamEntityList.add(indicatorViewPhysicalExamEntity);
            String indicatorFuncId = indicatorViewPhysicalExamEntity.getIndicatorFuncId();
            String peExperimentIndicatorFuncId = kIndicatorFuncIdVPEExperimentIndicatorFuncIdMap.get(indicatorFuncId);
            if (StringUtils.isBlank(peExperimentIndicatorFuncId)) {
              kIndicatorFuncIdVPEExperimentIndicatorFuncIdMap.put(indicatorFuncId, idGenerator.nextIdStr());
            }
          });
    }
    if (!indicatorViewPhysicalExamEntityList.isEmpty()) {
      indicatorViewPhysicalExamEntityList.forEach(indicatorViewPhysicalExamEntity -> {
        String experimentIndicatorViewPhysicalExamId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorViewPhysicalExamEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name = indicatorFuncEntity.getName();
        String peExperimentIndicatorFuncId = kIndicatorFuncIdVPEExperimentIndicatorFuncIdMap.get(indicatorFuncId);
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(peExperimentIndicatorFuncId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            if (!experimentIndicatorFuncIdList.contains(peExperimentIndicatorFuncId)) {
              experimentIndicatorFuncIdList.add(peExperimentIndicatorFuncId);
            }
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(peExperimentIndicatorFuncId, EnumIndicatorCategory.VIEW_MANAGEMENT_NO_REPORT_TWO_LEVEL.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(peExperimentIndicatorFuncId, name);
          });
        }
        String indicatorViewPhysicalExamId = indicatorViewPhysicalExamEntity.getIndicatorViewPhysicalExamId();
        String name1 = indicatorViewPhysicalExamEntity.getName();
        BigDecimal fee = indicatorViewPhysicalExamEntity.getFee();
        String indicatorInstanceId = indicatorViewPhysicalExamEntity.getIndicatorInstanceId();
        String resultAnalysis = indicatorViewPhysicalExamEntity.getResultAnalysis();
        Integer status = indicatorViewPhysicalExamEntity.getStatus();
        String indicatorCategoryId = null;
        String indicatorCategoryName = null;
        Date indicatorCategoryDt = null;
        IndicatorCategoryEntity indicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(indicatorViewPhysicalExamEntity.getIndicatorCategoryId());
        if (Objects.nonNull(indicatorCategoryEntity)) {
          indicatorCategoryId = indicatorCategoryEntity.getIndicatorCategoryId();
          indicatorCategoryName = indicatorCategoryEntity.getCategoryName();
          indicatorCategoryDt = indicatorCategoryEntity.getDt();
        }
        ExperimentIndicatorViewPhysicalExamRsEntity experimentIndicatorViewPhysicalExamRsEntity = ExperimentIndicatorViewPhysicalExamRsEntity
            .builder()
            .experimentIndicatorViewPhysicalExamId(experimentIndicatorViewPhysicalExamId)
            .indicatorViewPhysicalExamId(indicatorViewPhysicalExamId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .indicatorFuncId(peExperimentIndicatorFuncId)
            .name(name1)
            .fee(fee)
            .indicatorInstanceId(indicatorInstanceId)
            .resultAnalysis(resultAnalysis)
            .status(status)
            .indicatorCategoryId(indicatorCategoryId)
            .indicatorCategoryName(indicatorCategoryName)
            .indicatorCategoryDt(indicatorCategoryDt)
            .build();
        experimentIndicatorViewPhysicalExamRsEntityList.add(experimentIndicatorViewPhysicalExamRsEntity);
      });
    }
    /* runsix:查看指标-体格检查-二级类-无报告 */
    /* runsix:查看指标-辅助检查-四级类-无报告 */
    Map<String, String> kIndicatorFuncIdVSEExperimentIndicatorFuncIdMap = new HashMap<>();
    if (!funcIndicatorViewSupportExamIdSet.isEmpty()) {
      indicatorViewSupportExamService.lambdaQuery()
          .eq(IndicatorViewSupportExamEntity::getAppId, appId)
          .in(IndicatorViewSupportExamEntity::getIndicatorFuncId, funcIndicatorViewSupportExamIdSet)
          .list()
          .forEach(indicatorViewSupportExamEntity -> {
            indicatorViewSupportExamEntityList.add(indicatorViewSupportExamEntity);
            String indicatorFuncId = indicatorViewSupportExamEntity.getIndicatorFuncId();
            String seExperimentIndicatorFuncId = kIndicatorFuncIdVSEExperimentIndicatorFuncIdMap.get(indicatorFuncId);
            if (StringUtils.isBlank(seExperimentIndicatorFuncId)) {
              kIndicatorFuncIdVSEExperimentIndicatorFuncIdMap.put(indicatorFuncId, idGenerator.nextIdStr());
            }
          });
    }
    if (!indicatorViewSupportExamEntityList.isEmpty()) {
      indicatorViewSupportExamEntityList.forEach(indicatorViewSupportExamEntity -> {
        String experimentIndicatorViewSupportExamId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorViewSupportExamEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name = indicatorFuncEntity.getName();
        String seExperimentIndicatorFuncId = kIndicatorFuncIdVSEExperimentIndicatorFuncIdMap.get(indicatorFuncId);
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(seExperimentIndicatorFuncId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            if (!experimentIndicatorFuncIdList.contains(seExperimentIndicatorFuncId)) {
              experimentIndicatorFuncIdList.add(seExperimentIndicatorFuncId);
            }
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(seExperimentIndicatorFuncId, EnumIndicatorCategory.VIEW_MANAGEMENT_NO_REPORT_FOUR_LEVEL.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(seExperimentIndicatorFuncId, name);
          });
        }
        String indicatorViewSupportExamId = indicatorViewSupportExamEntity.getIndicatorViewSupportExamId();
        String name1 = indicatorViewSupportExamEntity.getName();
        BigDecimal fee = indicatorViewSupportExamEntity.getFee();
        String indicatorInstanceId = indicatorViewSupportExamEntity.getIndicatorInstanceId();
        String resultAnalysis = indicatorViewSupportExamEntity.getResultAnalysis();
        Integer status = indicatorViewSupportExamEntity.getStatus();
        String indicatorCategoryIdArray = null;
        List<String> indicatorCategoryIdList = new ArrayList<>();
        String indicatorCategoryNameArray = null;
        List<String> indicatorCategoryNameList = new ArrayList<>();
        String thirdIndicatorCategoryId = indicatorViewSupportExamEntity.getIndicatorCategoryId();
        String secondIndicatorCategoryId = kIndicatorCategoryIdVParentIndicatorCategoryIdMap.get(thirdIndicatorCategoryId);
        String firstIndicatorCategoryId = kIndicatorCategoryIdVParentIndicatorCategoryIdMap.get(secondIndicatorCategoryId);
        indicatorCategoryIdList.add(firstIndicatorCategoryId);
        indicatorCategoryIdList.add(secondIndicatorCategoryId);
        indicatorCategoryIdList.add(thirdIndicatorCategoryId);
        IndicatorCategoryEntity firstIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(firstIndicatorCategoryId);
        IndicatorCategoryEntity secondIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(secondIndicatorCategoryId);
        IndicatorCategoryEntity thirdIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(thirdIndicatorCategoryId);
        if (Objects.nonNull(firstIndicatorCategoryEntity) && Objects.nonNull(secondIndicatorCategoryEntity) && Objects.nonNull(thirdIndicatorCategoryEntity)) {
          indicatorCategoryNameList.add(firstIndicatorCategoryEntity.getCategoryName());
          indicatorCategoryNameList.add(secondIndicatorCategoryEntity.getCategoryName());
          indicatorCategoryNameList.add(thirdIndicatorCategoryEntity.getCategoryName());
        }
        indicatorCategoryIdArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryIdList);
        indicatorCategoryNameArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryNameList);
        ExperimentIndicatorViewSupportExamRsEntity experimentIndicatorViewSupportExamRsEntity = ExperimentIndicatorViewSupportExamRsEntity
            .builder()
            .experimentIndicatorViewSupportExamId(experimentIndicatorViewSupportExamId)
            .indicatorViewSupportExamId(indicatorViewSupportExamId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .indicatorFuncId(seExperimentIndicatorFuncId)
            .name(name1)
            .fee(fee)
            .indicatorInstanceId(indicatorInstanceId)
            .resultAnalysis(resultAnalysis)
            .status(status)
            .indicatorCategoryIdArray(indicatorCategoryIdArray)
            .indicatorCategoryNameArray(indicatorCategoryNameArray)
            .build();
        experimentIndicatorViewSupportExamRsEntityList.add(experimentIndicatorViewSupportExamRsEntity);
      });
    }
    /* runsix:查看指标-辅助检查-四级类-无报告 */
    /* runsix:TODO 判断指标-危险因素-二级类-无报告（有公式） */
    /* runsix:TODO 判断指标-危险因素-二级类-无报告（有公式） */
    /* runsix:判断指标-健康指导-二级类-有报告（无公式） */
    Map<String, String> kIndicatorFuncIdVHGExperimentIndicatorFuncIdMap = new HashMap<>();
    if (!funcIndicatorJudgeHealthGuidanceIdSet.isEmpty()) {
      indicatorJudgeHealthGuidanceService.lambdaQuery()
          .eq(IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
          .in(IndicatorJudgeHealthGuidanceEntity::getIndicatorFuncId, funcIndicatorJudgeHealthGuidanceIdSet)
          .list()
          .forEach(indicatorJudgeHealthGuidanceEntity -> {
            indicatorJudgeHealthGuidanceEntityList.add(indicatorJudgeHealthGuidanceEntity);
            String indicatorFuncId = indicatorJudgeHealthGuidanceEntity.getIndicatorFuncId();
            String hgExperimentIndicatorFuncId = kIndicatorFuncIdVHGExperimentIndicatorFuncIdMap.get(indicatorFuncId);
            if (StringUtils.isBlank(hgExperimentIndicatorFuncId)) {
              kIndicatorFuncIdVHGExperimentIndicatorFuncIdMap.put(indicatorFuncId, idGenerator.nextIdStr());
            }
          });
    }
    if (!indicatorJudgeHealthGuidanceEntityList.isEmpty()) {
      indicatorJudgeHealthGuidanceEntityList.forEach(indicatorJudgeHealthGuidanceEntity -> {
        String experimentIndicatorJudgeHealthGuidanceId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorJudgeHealthGuidanceEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name = indicatorFuncEntity.getName();
        String hgExperimentIndicatorFuncId = kIndicatorFuncIdVHGExperimentIndicatorFuncIdMap.get(indicatorFuncId);
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(hgExperimentIndicatorFuncId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            if (!experimentIndicatorFuncIdList.contains(hgExperimentIndicatorFuncId)) {
              experimentIndicatorFuncIdList.add(hgExperimentIndicatorFuncId);
            }
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(hgExperimentIndicatorFuncId, EnumIndicatorCategory.JUDGE_MANAGEMENT_HEALTH_PROBLEM.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(hgExperimentIndicatorFuncId, name);
          });
        }
        String indicatorJudgeHealthGuidanceId = indicatorJudgeHealthGuidanceEntity.getIndicatorJudgeHealthGuidanceId();
        String name1 = indicatorJudgeHealthGuidanceEntity.getName();
        BigDecimal point = indicatorJudgeHealthGuidanceEntity.getPoint();
        String resultExplain = indicatorJudgeHealthGuidanceEntity.getResultExplain();
        Integer status = indicatorJudgeHealthGuidanceEntity.getStatus();
        String indicatorCategoryIdArray = null;
        List<String> indicatorCategoryIdList = new ArrayList<>();
        String indicatorCategoryNameArray = null;
        List<String> indicatorCategoryNameList = new ArrayList<>();
        String firstIndicatorCategoryId = indicatorJudgeHealthGuidanceEntity.getIndicatorCategoryId();
        indicatorCategoryIdList.add(firstIndicatorCategoryId);
        IndicatorCategoryEntity firstIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(firstIndicatorCategoryId);
        if (Objects.nonNull(firstIndicatorCategoryEntity)) {
          indicatorCategoryNameList.add(firstIndicatorCategoryEntity.getCategoryName());
        }
        indicatorCategoryIdArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryIdList);
        indicatorCategoryNameArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryNameList);
        ExperimentIndicatorJudgeHealthGuidanceRsEntity experimentIndicatorJudgeHealthGuidanceRsEntity = ExperimentIndicatorJudgeHealthGuidanceRsEntity
            .builder()
            .experimentIndicatorJudgeHealthGuidanceId(experimentIndicatorJudgeHealthGuidanceId)
            .indicatorJudgeHealthGuidanceId(indicatorJudgeHealthGuidanceId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .indicatorFuncId(hgExperimentIndicatorFuncId)
            .name(name1)
            .point(point)
            .resultExplain(resultExplain)
            .status(status)
            .indicatorCategoryIdArray(indicatorCategoryIdArray)
            .indicatorCategoryNameArray(indicatorCategoryNameArray)
            .build();
        experimentIndicatorJudgeHealthGuidanceRsEntityList.add(experimentIndicatorJudgeHealthGuidanceRsEntity);
      });
    }
    /* runsix:判断指标-健康指导-二级类-有报告（无公式） */
    /* runsix:判断指标-健康问题-三级类-无报告（无公式） */
    Map<String, String> kIndicatorFuncIdVHPExperimentIndicatorFuncIdMap = new HashMap<>();
    if (!funcIndicatorJudgeHealthProblemIdSet.isEmpty()) {
      indicatorJudgeHealthProblemService.lambdaQuery()
          .eq(IndicatorJudgeHealthProblemEntity::getAppId, appId)
          .in(IndicatorJudgeHealthProblemEntity::getIndicatorFuncId, funcIndicatorJudgeHealthProblemIdSet)
          .list()
          .forEach(indicatorJudgeHealthProblemEntity -> {
            indicatorJudgeHealthProblemEntityList.add(indicatorJudgeHealthProblemEntity);
            String indicatorFuncId = indicatorJudgeHealthProblemEntity.getIndicatorFuncId();
            String hpExperimentIndicatorFuncId = kIndicatorFuncIdVHPExperimentIndicatorFuncIdMap.get(indicatorFuncId);
            if (StringUtils.isBlank(hpExperimentIndicatorFuncId)) {
              kIndicatorFuncIdVHPExperimentIndicatorFuncIdMap.put(indicatorFuncId, idGenerator.nextIdStr());
            }
          });
    }
    if (!indicatorJudgeHealthProblemEntityList.isEmpty()) {
      indicatorJudgeHealthProblemEntityList.forEach(indicatorJudgeHealthProblemEntity -> {
        String experimentIndicatorJudgeHealthProblemId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorJudgeHealthProblemEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name = indicatorFuncEntity.getName();
        String hpExperimentIndicatorFuncId = kIndicatorFuncIdVHPExperimentIndicatorFuncIdMap.get(indicatorFuncId);
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(hpExperimentIndicatorFuncId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            if (!experimentIndicatorFuncIdList.contains(hpExperimentIndicatorFuncId)) {
              experimentIndicatorFuncIdList.add(hpExperimentIndicatorFuncId);
            }
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(hpExperimentIndicatorFuncId, EnumIndicatorCategory.JUDGE_MANAGEMENT_HEALTH_PROBLEM.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(hpExperimentIndicatorFuncId, name);
          });
        }
        String indicatorJudgeHealthProblemId = indicatorJudgeHealthProblemEntity.getIndicatorJudgeHealthProblemId();
        String name1 = indicatorJudgeHealthProblemEntity.getName();
        BigDecimal point = indicatorJudgeHealthProblemEntity.getPoint();
        String resultExplain = indicatorJudgeHealthProblemEntity.getResultExplain();
        Integer status = indicatorJudgeHealthProblemEntity.getStatus();
        String indicatorCategoryIdArray = null;
        List<String> indicatorCategoryIdList = new ArrayList<>();
        String indicatorCategoryNameArray = null;
        List<String> indicatorCategoryNameList = new ArrayList<>();
        String secondIndicatorCategoryId = indicatorJudgeHealthProblemEntity.getIndicatorCategoryId();
        String firstIndicatorCategoryId = kIndicatorCategoryIdVParentIndicatorCategoryIdMap.get(secondIndicatorCategoryId);
        indicatorCategoryIdList.add(firstIndicatorCategoryId);
        indicatorCategoryIdList.add(secondIndicatorCategoryId);
        IndicatorCategoryEntity firstIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(firstIndicatorCategoryId);
        IndicatorCategoryEntity secondIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(secondIndicatorCategoryId);
        if (Objects.nonNull(firstIndicatorCategoryEntity) && Objects.nonNull(secondIndicatorCategoryEntity)) {
          indicatorCategoryNameList.add(firstIndicatorCategoryEntity.getCategoryName());
          indicatorCategoryNameList.add(secondIndicatorCategoryEntity.getCategoryName());
        }
        indicatorCategoryIdArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryIdList);
        indicatorCategoryNameArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryNameList);
        ExperimentIndicatorJudgeHealthProblemRsEntity experimentIndicatorJudgeHealthProblemRsEntity = ExperimentIndicatorJudgeHealthProblemRsEntity
            .builder()
            .experimentIndicatorJudgeHealthProblemId(experimentIndicatorJudgeHealthProblemId)
            .indicatorJudgeHealthProblemId(indicatorJudgeHealthProblemId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .indicatorFuncId(hpExperimentIndicatorFuncId)
            .name(name1)
            .point(point)
            .resultExplain(resultExplain)
            .status(status)
            .indicatorCategoryIdArray(indicatorCategoryIdArray)
            .indicatorCategoryNameArray(indicatorCategoryNameArray)
            .build();
        experimentIndicatorJudgeHealthProblemRsEntityList.add(experimentIndicatorJudgeHealthProblemRsEntity);
      });
    }
    /* runsix:判断指标-健康问题-三级类-无报告（无公式） */
    /* runsix:判断指标-疾病问题-四级类-无报告（无公式） */
    Map<String, String> kIndicatorFuncIdVDPExperimentIndicatorFuncIdMap = new HashMap<>();
    if (!funcIndicatorJudgeDiseaseProblemIdSet.isEmpty()) {
      indicatorJudgeDiseaseProblemService.lambdaQuery()
          .eq(IndicatorJudgeDiseaseProblemEntity::getAppId, appId)
          .in(IndicatorJudgeDiseaseProblemEntity::getIndicatorFuncId, funcIndicatorJudgeDiseaseProblemIdSet)
          .list()
          .forEach(indicatorJudgeDiseaseProblemEntity -> {
            indicatorJudgeDiseaseProblemEntityList.add(indicatorJudgeDiseaseProblemEntity);
            String indicatorFuncId = indicatorJudgeDiseaseProblemEntity.getIndicatorFuncId();
            String dpExperimentIndicatorFuncId = kIndicatorFuncIdVDPExperimentIndicatorFuncIdMap.get(indicatorFuncId);
            if (StringUtils.isBlank(dpExperimentIndicatorFuncId)) {
              kIndicatorFuncIdVDPExperimentIndicatorFuncIdMap.put(indicatorFuncId, idGenerator.nextIdStr());
            }
          });
    }
    if (!indicatorJudgeDiseaseProblemEntityList.isEmpty()) {
      indicatorJudgeDiseaseProblemEntityList.forEach(indicatorJudgeDiseaseProblemEntity -> {
        String experimentIndicatorJudgeDiseaseProblemId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorJudgeDiseaseProblemEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name = indicatorFuncEntity.getName();
        String dpExperimentIndicatorFuncId = kIndicatorFuncIdVDPExperimentIndicatorFuncIdMap.get(indicatorFuncId);
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(dpExperimentIndicatorFuncId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            if (!experimentIndicatorFuncIdList.contains(dpExperimentIndicatorFuncId)) {
              experimentIndicatorFuncIdList.add(dpExperimentIndicatorFuncId);
            }
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(dpExperimentIndicatorFuncId, EnumIndicatorCategory.JUDGE_MANAGEMENT_DISEASE_PROBLEM.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(dpExperimentIndicatorFuncId, name);
          });
        }
        String indicatorJudgeDiseaseProblemId = indicatorJudgeDiseaseProblemEntity.getIndicatorJudgeDiseaseProblemId();
        String name1 = indicatorJudgeDiseaseProblemEntity.getName();
        BigDecimal point = indicatorJudgeDiseaseProblemEntity.getPoint();
        String resultExplain = indicatorJudgeDiseaseProblemEntity.getResultExplain();
        Integer status = indicatorJudgeDiseaseProblemEntity.getStatus();
        String indicatorCategoryIdArray = null;
        List<String> indicatorCategoryIdList = new ArrayList<>();
        String indicatorCategoryNameArray = null;
        List<String> indicatorCategoryNameList = new ArrayList<>();
        String thirdIndicatorCategoryId = indicatorJudgeDiseaseProblemEntity.getIndicatorCategoryId();
        String secondIndicatorCategoryId = kIndicatorCategoryIdVParentIndicatorCategoryIdMap.get(thirdIndicatorCategoryId);
        String firstIndicatorCategoryId = kIndicatorCategoryIdVParentIndicatorCategoryIdMap.get(secondIndicatorCategoryId);
        indicatorCategoryIdList.add(firstIndicatorCategoryId);
        indicatorCategoryIdList.add(secondIndicatorCategoryId);
        indicatorCategoryIdList.add(thirdIndicatorCategoryId);
        IndicatorCategoryEntity firstIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(firstIndicatorCategoryId);
        IndicatorCategoryEntity secondIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(secondIndicatorCategoryId);
        IndicatorCategoryEntity thirdIndicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(thirdIndicatorCategoryId);
        if (Objects.nonNull(firstIndicatorCategoryEntity) && Objects.nonNull(secondIndicatorCategoryEntity) && Objects.nonNull(thirdIndicatorCategoryEntity)) {
          indicatorCategoryNameList.add(firstIndicatorCategoryEntity.getCategoryName());
          indicatorCategoryNameList.add(secondIndicatorCategoryEntity.getCategoryName());
          indicatorCategoryNameList.add(thirdIndicatorCategoryEntity.getCategoryName());
        }
        indicatorCategoryIdArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryIdList);
        indicatorCategoryNameArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryNameList);
        ExperimentIndicatorJudgeDiseaseProblemRsEntity experimentIndicatorJudgeDiseaseProblemRsEntity = ExperimentIndicatorJudgeDiseaseProblemRsEntity
            .builder()
            .experimentIndicatorJudgeDiseaseProblemId(experimentIndicatorJudgeDiseaseProblemId)
            .indicatorJudgeDiseaseProblemId(indicatorJudgeDiseaseProblemId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .indicatorFuncId(dpExperimentIndicatorFuncId)
            .name(name1)
            .point(point)
            .resultExplain(resultExplain)
            .status(status)
            .indicatorCategoryIdArray(indicatorCategoryIdArray)
            .indicatorCategoryNameArray(indicatorCategoryNameArray)
            .build();
        experimentIndicatorJudgeDiseaseProblemRsEntityList.add(experimentIndicatorJudgeDiseaseProblemRsEntity);
      });
    }
    /* runsix:判断指标-疾病问题-四级类-无报告（无公式） */
    /* runsix:复制机构功能模块以及功能点 */
    caseOrgEntityList.forEach(caseOrgEntity -> {
      String caseOrgId = caseOrgEntity.getCaseOrgId();
      List<CaseOrgModuleEntity> caseOrgModuleEntityList1 = kCaseOrgIdVCaseOrgModuleEntityListMap.get(caseOrgId);
      if (Objects.nonNull(caseOrgModuleEntityList1) && !caseOrgModuleEntityList1.isEmpty()) {
        caseOrgModuleEntityList1.forEach(caseOrgModuleEntity -> {
          String caseOrgId1 = caseOrgModuleEntity.getCaseOrgId();
          List<ExperimentOrgEntity> experimentOrgEntityList1 = kCaseOrgIdVExperimentOrgEntityListMap.get(caseOrgId1);
          if (Objects.nonNull(experimentOrgEntityList1) && !experimentOrgEntityList1.isEmpty()) {
            experimentOrgEntityList1.forEach(experimentOrgEntity1 -> {
              String experimentOrgModuleId = idGenerator.nextIdStr();
              String caseOrgModuleId = caseOrgModuleEntity.getCaseOrgModuleId();
              String indicatorFuncIdArray = null;
              String indicatorFuncNameArray = null;
              String indicatorCategoryIdArray = null;
              List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
              if (Objects.nonNull(experimentIndicatorFuncIdList) && !experimentIndicatorFuncIdList.isEmpty()) {
                experimentIndicatorFuncIdList.sort(Comparator.comparingInt(experimentIndicatorFuncId -> {
                  String indicatorFuncId = kExperimentIndicatorFuncIdVIndicatorFuncIdMap.get(experimentIndicatorFuncId);
                  if (StringUtils.isBlank(indicatorFuncId)) {
                    return 0;
                  }
                  Map<String, Integer> aMap = kIndicatorFuncIdVKCaseOrgModuleIdVSeqMap.get(indicatorFuncId);
                  if (Objects.isNull(aMap)) {
                    return 0;
                  }
                  return aMap.get(caseOrgModuleId);
                }));
                indicatorFuncIdArray = String.join(EnumString.COMMA.getStr(), experimentIndicatorFuncIdList);
                List<String> indicatorFuncNameList =  new ArrayList<>();
                List<String> indicatorCategoryIdList = new ArrayList<>();
                experimentIndicatorFuncIdList.forEach(experimentIndicatorFuncId -> {
                  String indicatorFuncName = kExperimentIndicatorFuncIdVIndicatorFuncNameMap.get(experimentIndicatorFuncId);
                  if (StringUtils.isBlank(indicatorFuncName)) {
                    indicatorFuncName = "被删除";
                  }
                  indicatorFuncNameList.add(indicatorFuncName);
                  String indicatorCategoryId = kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.get(experimentIndicatorFuncId);
                  if (StringUtils.isBlank(indicatorCategoryId)) {
                    indicatorCategoryId = "被删除";
                  }
                  indicatorCategoryIdList.add(indicatorCategoryId);
                });
                indicatorFuncNameArray = String.join(EnumString.COMMA.getStr(), indicatorFuncNameList);
                indicatorCategoryIdArray = String.join(EnumString.COMMA.getStr(), indicatorCategoryIdList);
              }
              ExperimentOrgModuleRsEntity experimentOrgModuleRsEntity = ExperimentOrgModuleRsEntity
                  .builder()
                  .experimentOrgModuleId(experimentOrgModuleId)
                  .caseOrgModuleId(caseOrgModuleId)
                  .appId(appId)
                  .orgId(experimentOrgEntity1.getExperimentOrgId())
                  .name(caseOrgModuleEntity.getName())
                  .indicatorFuncIdArray(indicatorFuncIdArray)
                  .indicatorFuncNameArray(indicatorFuncNameArray)
                  .indicatorCategoryIdArray(indicatorCategoryIdArray)
                  .seq(caseOrgModuleEntity.getSeq())
                  .build();
              experimentOrgModuleRsEntityList.add(experimentOrgModuleRsEntity);
            });
          }
        });
      }
    });
    /* runsix:复制机构功能模块以及功能点 */
    experimentOrgModuleRsService.saveOrUpdateBatch(experimentOrgModuleRsEntityList);
    experimentIndicatorViewBaseInfoRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoRsEntityList);
    experimentIndicatorViewBaseInfoDescrRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoDescRsEntityList);
    experimentIndicatorViewBaseInfoMonitorRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoMonitorRsEntityList);
    experimentIndicatorViewBaseInfoSingleRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoSingleRsEntityList);
    experimentIndicatorViewPhysicalExamRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamRsEntityList);
    experimentIndicatorViewSupportExamRsService.saveOrUpdateBatch(experimentIndicatorViewSupportExamRsEntityList);
    experimentIndicatorJudgeRiskFactorRsService.saveOrUpdateBatch(experimentIndicatorJudgeRiskFactorRsEntityList);
    experimentIndicatorJudgeHealthGuidanceRsService.saveOrUpdateBatch(experimentIndicatorJudgeHealthGuidanceRsEntityList);
    experimentIndicatorJudgeHealthProblemRsService.saveOrUpdateBatch(experimentIndicatorJudgeHealthProblemRsEntityList);
    experimentIndicatorJudgeDiseaseProblemRsService.saveOrUpdateBatch(experimentIndicatorJudgeDiseaseProblemRsEntityList);
  }

  /**
   * TODO 这里可能因为uim有大问题, CasePersonEntity的account_id才跟指标绑定
   * 数据库添加的人物，account_id是数据库人物管理通过uim传过来的，在case_indicator_instance绑定的principal_id
   * 自定义添加的人物，account_id是在uim新增人物，然后uim传过来的
   * 1.先查出实验人物
   * 2.根据实验人物查出案例人物
   * 3.根据案例人物查出案例人物的account_id
   * 4.根据案例人物的account_id查出案例指标
   * runsix method process
   * 1.ExperimentIndicatorInstanceRsEntity
   * 2.ExperimentIndicatorValRsEntity
  */
  @Transactional(rollbackFor = Exception.class)
  public void rsCopyPersonIndicator(RsCopyPersonIndicatorRequestRs rsCopyPersonIndicatorRequestRs) {
    List<ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorValRsEntity> experimentIndicatorValRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorExpressionRefRsEntity> experimentIndicatorExpressionRefRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
    List<ExperimentIndicatorExpressionInfluenceRsEntity> experimentIndicatorExpressionInfluenceRsEntityList = new ArrayList<>();
    Map<String, Map<String, String>> kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorInstanceIdVExperimentPersonIdMap = new HashMap<>();
    Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = new HashMap<>();
    String experimentInstanceId = rsCopyPersonIndicatorRequestRs.getExperimentInstanceId();
    String caseInstanceId = rsCopyPersonIndicatorRequestRs.getCaseInstanceId();
    String appId = rsCopyPersonIndicatorRequestRs.getAppId();
    Set<String> experimentPersonIdSet = new HashSet<>();
    Set<String> casePersonIdSet = new HashSet<>();
    Set<String> principalIdSet = new HashSet<>();
    Map<String, ExperimentPersonEntity> kExperimentPersonIdVExperimentPersonEntityMap = new HashMap<>();
    Map<String, List<CaseIndicatorInstanceEntity>> kPrincipalIdVCaseIndicatorInstanceEntityListMap = new HashMap<>();
    Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
    Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = new HashMap<>();
    Map<String, String> kCasePersonIdVAccountIdMap = new HashMap<>();
    Map<String, List<String>> kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap = new HashMap<>();
    Integer maxPeriods = rsCopyPersonIndicatorRequestRs.getPeriods();
    experimentPersonService.lambdaQuery()
      .eq(ExperimentPersonEntity::getAppId, appId)
      .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentInstanceId)
      .list()
      .forEach(experimentPersonEntity -> {
        casePersonIdSet.add(experimentPersonEntity.getCasePersonId());
        experimentPersonIdSet.add(experimentPersonEntity.getExperimentPersonId());
        kExperimentPersonIdVExperimentPersonEntityMap.put(experimentPersonEntity.getExperimentPersonId(), experimentPersonEntity);
      });
    if (experimentPersonIdSet.isEmpty() || casePersonIdSet.isEmpty()) {
      log.warn("method rsCopyPersonIndicator experimentInstanceId:{} has no person", experimentInstanceId);
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    casePersonService.lambdaQuery()
      .in(CasePersonEntity::getCasePersonId, casePersonIdSet)
      .list()
      .forEach(casePersonEntity -> {
        kCasePersonIdVAccountIdMap.put(casePersonEntity.getCasePersonId(), casePersonEntity.getAccountId());
        principalIdSet.add(casePersonEntity.getAccountId());
      });
    if (principalIdSet.isEmpty()) {
      log.warn("method rsCopyPersonIndicator principalIdSet:is empty");
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    /* runsix:defined by yourself */
    caseIndicatorInstanceService.lambdaQuery()
      .eq(CaseIndicatorInstanceEntity::getAppId, appId)
      .in(CaseIndicatorInstanceEntity::getPrincipalId, principalIdSet)
      .list()
      .forEach(caseIndicatorInstanceEntity -> {
        caseIndicatorInstanceIdSet.add(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
        List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = kPrincipalIdVCaseIndicatorInstanceEntityListMap.get(caseIndicatorInstanceEntity.getPrincipalId());
        if (Objects.isNull(caseIndicatorInstanceEntityList)) {
          caseIndicatorInstanceEntityList = new ArrayList<>();
        }
        caseIndicatorInstanceEntityList.add(caseIndicatorInstanceEntity);
        kPrincipalIdVCaseIndicatorInstanceEntityListMap.put(caseIndicatorInstanceEntity.getPrincipalId(), caseIndicatorInstanceEntityList);
      });
    if (caseIndicatorInstanceIdSet.isEmpty()) {
      log.warn("method rsCopyPersonIndicator caseInstanceId:{} has no indicator", caseInstanceId);
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    caseIndicatorRuleService.lambdaQuery()
      .eq(CaseIndicatorRuleEntity::getAppId, appId)
      .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
      .list()
      .forEach(caseIndicatorRuleEntity -> {
        kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.put(caseIndicatorRuleEntity.getVariableId(), caseIndicatorRuleEntity);
      });
    kExperimentPersonIdVExperimentPersonEntityMap.forEach((experimentPersonId, experimentPersonEntity) -> {
      String casePersonId = experimentPersonEntity.getCasePersonId();
      String accountId = kCasePersonIdVAccountIdMap.get(casePersonId);
      List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = kPrincipalIdVCaseIndicatorInstanceEntityListMap.get(accountId);
      if (Objects.isNull(caseIndicatorInstanceEntityList)) {
        log.warn("method rsCopyPersonIndicator casePersonId:{}, accountId:{} has no indicator", casePersonId, accountId);
        throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
      }
      caseIndicatorInstanceEntityList.forEach(caseIndicatorInstanceEntity -> {
        String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
        String min = null;
        String max = null;
        String def = null;
        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
        if (Objects.nonNull(caseIndicatorRuleEntity)) {
          min = caseIndicatorRuleEntity.getMin();
          max = caseIndicatorRuleEntity.getMax();
          def = caseIndicatorRuleEntity.getDef();
        }
        String experimentIndicatorInstanceId = idGenerator.nextIdStr();
        List<String> experimentIndicatorInstanceIdList = kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap.get(caseIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorInstanceIdList)) {
          experimentIndicatorInstanceIdList = new ArrayList<>();
        }
        experimentIndicatorInstanceIdList.add(experimentIndicatorInstanceId);
        kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap.put(caseIndicatorInstanceId, experimentIndicatorInstanceIdList);
        Map<String, String> kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
        if (Objects.isNull(kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
          kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
        }
        kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(caseIndicatorInstanceId, experimentIndicatorInstanceId);
        kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentPersonId, kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap);
        kExperimentIndicatorInstanceIdVExperimentPersonIdMap.put(experimentIndicatorInstanceId, experimentPersonId);
        experimentIndicatorInstanceRsEntityList.add(
            ExperimentIndicatorInstanceRsEntity
                .builder()
                .experimentIndicatorInstanceId(experimentIndicatorInstanceId)
                .caseIndicatorInstanceId(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId())
                .indicatorInstanceId(caseIndicatorInstanceEntity.getIndicatorInstanceId())
                .experimentId(experimentInstanceId)
                .caseId(caseInstanceId)
                .experimentPersonId(experimentPersonId)
                .indicatorName(caseIndicatorInstanceEntity.getIndicatorName())
                .displayByPercent(caseIndicatorInstanceEntity.getDisplayByPercent())
                .unit(caseIndicatorInstanceEntity.getUnit())
                .core(caseIndicatorInstanceEntity.getCore())
                .food(caseIndicatorInstanceEntity.getFood())
                .descr(caseIndicatorInstanceEntity.getDescr())
                .min(min)
                .max(max)
                .def(def)
                .experimentIndicatorExpressionId(null)
                .build()
        );
      });
    });
    experimentIndicatorInstanceRsEntityList.forEach(experimentIndicatorInstanceRsEntity -> {
      for (int i = 1; i <= maxPeriods; i++) {
        experimentIndicatorValRsEntityList.add(
            ExperimentIndicatorValRsEntity
                .builder()
                .experimentIndicatorValId(idGenerator.nextIdStr())
                .experimentId(experimentInstanceId)
                .caseId(caseInstanceId)
                .indicatorInstanceId(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())
                .currentVal(experimentIndicatorInstanceRsEntity.getDef())
                .periods(i)
                .min(experimentIndicatorInstanceRsEntity.getMin())
                .max(experimentIndicatorInstanceRsEntity.getMax())
                .descr(experimentIndicatorInstanceRsEntity.getDescr())
                .build()
        );
      }
    });
    Map<String, List<String>> kCaseReasonIdVIndicatorExpressionIdListMap = new HashMap<>();
    Set<String> caseIndicatorExpressionIdSet = new HashSet<>();
    if (!caseIndicatorInstanceIdSet.isEmpty()) {
      caseIndicatorExpressionRefService.lambdaQuery()
          .eq(CaseIndicatorExpressionRefEntity::getAppId, appId)
          .in(CaseIndicatorExpressionRefEntity::getReasonId, caseIndicatorInstanceIdSet)
          .list()
          .forEach(caseIndicatorExpressionRefEntity -> {
            caseIndicatorExpressionIdSet.add(caseIndicatorExpressionRefEntity.getIndicatorExpressionId());
            String reasonId = caseIndicatorExpressionRefEntity.getReasonId();
            List<String> indicatorExpressionIdList = kCaseReasonIdVIndicatorExpressionIdListMap.get(reasonId);
            if (Objects.isNull(indicatorExpressionIdList)) {
              indicatorExpressionIdList = new ArrayList<>();
            }
            indicatorExpressionIdList.add(caseIndicatorExpressionRefEntity.getIndicatorExpressionId());
            kCaseReasonIdVIndicatorExpressionIdListMap.put(reasonId, indicatorExpressionIdList);
          });
    }
    Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap = new HashMap<>();
    Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap = new HashMap<>();
    Map<String, CaseIndicatorExpressionInfluenceEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap = new HashMap<>();
    if (!caseIndicatorExpressionIdSet.isEmpty()) {
      caseIndicatorExpressionService.lambdaQuery()
          .eq(CaseIndicatorExpressionEntity::getAppId, appId)
          .in(CaseIndicatorExpressionEntity::getIndicatorExpressionId, caseIndicatorExpressionIdSet)
          .list()
          .forEach(caseIndicatorExpressionEntity -> {
            kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.put(caseIndicatorExpressionEntity.getCaseIndicatorExpressionId(), caseIndicatorExpressionEntity);
          });
      caseIndicatorExpressionItemService.lambdaQuery()
          .eq(CaseIndicatorExpressionItemEntity::getAppId, appId)
          .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionIdSet)
          .list()
          .forEach(caseIndicatorExpressionItemEntity -> {
            kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.put(caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId(), caseIndicatorExpressionItemEntity);
            String indicatorExpressionId = caseIndicatorExpressionItemEntity.getIndicatorExpressionId();
            List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
            if (Objects.isNull(caseIndicatorExpressionItemEntityList)) {
              caseIndicatorExpressionItemEntityList = new ArrayList<>();
            }
            caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionItemEntity);
            kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, caseIndicatorExpressionItemEntityList);
          });
      caseIndicatorExpressionInfluenceService.lambdaQuery()
          .eq(CaseIndicatorExpressionInfluenceEntity::getAppId, appId)
          .in(CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, caseIndicatorExpressionIdSet)
          .list()
          .forEach(caseIndicatorExpressionInfluenceEntity -> {
            kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap.put(caseIndicatorExpressionInfluenceEntity.getIndicatorInstanceId(), caseIndicatorExpressionInfluenceEntity);
          });
    }
    caseIndicatorInstanceIdSet.forEach(caseIndicatorInstanceId -> {
      List<String> experimentIndicatorInstanceIdList = kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdListMap.get(caseIndicatorInstanceId);
      if (Objects.isNull(experimentIndicatorInstanceIdList) || experimentIndicatorInstanceIdList.isEmpty()) {
        log.warn("caseIndicatorInstanceId:{} has no experimentIndicatorInstanceIdList",  caseIndicatorInstanceId);
        throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
      }
      experimentIndicatorInstanceIdList.forEach(experimentIndicatorInstanceId -> {
        String experimentPersonId = kExperimentIndicatorInstanceIdVExperimentPersonIdMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentPersonId)) {
          log.warn("1");
          return;
        }
        String experimentIndicatorExpressionRefId = idGenerator.nextIdStr();
        List<String> caseIndicatorExpressionIdList = kCaseReasonIdVIndicatorExpressionIdListMap.get(caseIndicatorInstanceId);
        if (Objects.isNull(caseIndicatorExpressionIdList) || caseIndicatorExpressionIdList.isEmpty()) {
          log.warn("2");
          return;
        }
        caseIndicatorExpressionIdList.forEach(caseIndicatorExpressionId -> {
          CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.get(caseIndicatorExpressionId);
          if (Objects.isNull(caseIndicatorExpressionEntity)) {
            log.warn("3");
            return;
          }
          String experimentIndicatorExpressionId = idGenerator.nextIdStr();
          String caseMaxIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId();
          CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity = kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseMaxIndicatorExpressionItemId);
          String maxIndicatorExpressionItemId = idGenerator.nextIdStr();
          ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = getExperimentIndicatorExpressionItemRsEntity(
              kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
              maxCaseIndicatorExpressionItemEntity,
              experimentPersonId,
              experimentInstanceId,
              caseInstanceId,
              appId,
              experimentIndicatorExpressionId
          );
          experimentIndicatorExpressionItemRsEntityList.add(maxExperimentIndicatorExpressionItemRsEntity);
          String caseMinIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId();
          CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity = kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseMinIndicatorExpressionItemId);
          String minIndicatorExpressionItemId = idGenerator.nextIdStr();
          ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = getExperimentIndicatorExpressionItemRsEntity(
              kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
              minCaseIndicatorExpressionItemEntity,
              experimentPersonId,
              experimentInstanceId,
              caseInstanceId,
              appId,
              experimentIndicatorExpressionId
          );
          experimentIndicatorExpressionItemRsEntityList.add(minExperimentIndicatorExpressionItemRsEntity);
          List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.get(caseIndicatorExpressionId);
          if (Objects.isNull(caseIndicatorExpressionItemEntityList) || caseIndicatorExpressionItemEntityList.isEmpty()) {
            return;
          }
          caseIndicatorExpressionItemEntityList.forEach(caseIndicatorExpressionItemEntity -> {
            ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = getExperimentIndicatorExpressionItemRsEntity(
                kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
                caseIndicatorExpressionItemEntity,
                experimentPersonId,
                experimentInstanceId,
                caseInstanceId,
                appId,
                experimentIndicatorExpressionId
            );
            experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
          });
          ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = ExperimentIndicatorExpressionRsEntity
              .builder()
              .experimentIndicatorExpressionId(experimentIndicatorExpressionId)
              .caseIndicatorExpressionId(caseIndicatorExpressionId)
              .experimentId(experimentInstanceId)
              .caseId(caseInstanceId)
              .principalId(experimentIndicatorInstanceId)
              .reasonId(experimentIndicatorInstanceId)
              .maxIndicatorExpressionItemId(maxIndicatorExpressionItemId)
              .minIndicatorExpressionItemId(minIndicatorExpressionItemId)
              .type(caseIndicatorExpressionEntity.getType())
              .source(caseIndicatorExpressionEntity.getSource())
              .build();
          experimentIndicatorExpressionRsEntityList.add(experimentIndicatorExpressionRsEntity);
        });
        ExperimentIndicatorExpressionRefRsEntity experimentIndicatorExpressionRefRsEntity = ExperimentIndicatorExpressionRefRsEntity
            .builder()
            .experimentIndicatorExpressionRefId(experimentIndicatorExpressionRefId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .indicatorExpressionId(experimentIndicatorInstanceId)
            .reasonId(experimentIndicatorInstanceId)
            .build();
        experimentIndicatorExpressionRefRsEntityList.add(experimentIndicatorExpressionRefRsEntity);
      });
    });
    experimentIndicatorInstanceRsService.saveOrUpdateBatch(experimentIndicatorInstanceRsEntityList);
    experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
    experimentIndicatorExpressionRefRsService.saveOrUpdateBatch(experimentIndicatorExpressionRefRsEntityList);
    experimentIndicatorExpressionRsService.saveOrUpdateBatch(experimentIndicatorExpressionRsEntityList);
    experimentIndicatorExpressionItemRsService.saveOrUpdateBatch(experimentIndicatorExpressionItemRsEntityList);
    experimentIndicatorExpressionInfluenceRsService.saveOrUpdateBatch(experimentIndicatorExpressionInfluenceRsEntityList);
  }

  private String getExperimentIndicatorInstanceIdByCaseIndicatorInstanceId(String caseIndicatorInstanceId, String experimentPersonId, Map<String, Map<String, String>> kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap) {
    if (Objects.isNull(kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
      return null;
    }
    Map<String, String> kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
    if (Objects.isNull(kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
      return null;
    }
    return kCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(caseIndicatorInstanceId);
  }

  private ExperimentIndicatorExpressionItemRsEntity getExperimentIndicatorExpressionItemRsEntity(
      Map<String, Map<String, String>> kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity,
      String experimentPersonId,
      String experimentInstanceId,
      String caseInstanceId,
      String appId,
      String experimentIndicatorExpressionId
      ) {
    String experimentIndicatorExpressionItemId = idGenerator.nextIdStr();
    String caseConditionValList = caseIndicatorExpressionItemEntity.getConditionValList();
    String experimentConditionValList = null;
    if (StringUtils.isNotBlank(caseConditionValList)) {
      List<String> caseConditionValListSplit = Arrays.stream(caseConditionValList.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
      List<String> experimentConditionValListSplit = new ArrayList<>();
      caseConditionValListSplit.forEach(caseIndicatorInstanceId1 -> {
        String experimentIndicatorInstanceId1 = getExperimentIndicatorInstanceIdByCaseIndicatorInstanceId(caseIndicatorInstanceId1, experimentPersonId, kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap);
        if (StringUtils.isNotBlank(experimentIndicatorInstanceId1)) {
          experimentConditionValListSplit.add(experimentIndicatorInstanceId1);
        }
      });
      experimentConditionValList = String.join(EnumString.COMMA.getStr(), experimentConditionValListSplit);
    }
    String caseResultValList = caseIndicatorExpressionItemEntity.getResultValList();
    String experimentResultValList = null;
    if (StringUtils.isNotBlank(caseResultValList)) {
      List<String> caseResultValListSplit = Arrays.stream(caseResultValList.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
      List<String> experimentResultValListSplit = new ArrayList<>();
      caseResultValListSplit.forEach(caseIndicatorInstanceId1 -> {
        String experimentIndicatorInstanceId1 = getExperimentIndicatorInstanceIdByCaseIndicatorInstanceId(caseIndicatorInstanceId1, experimentPersonId, kExperimentPersonIdVKCaseIndicatorInstanceIdVExperimentIndicatorInstanceIdMap);
        if (StringUtils.isNotBlank(experimentIndicatorInstanceId1)) {
          experimentResultValListSplit.add(experimentIndicatorInstanceId1);
        }
      });
      experimentResultValList = String.join(EnumString.COMMA.getStr(), experimentResultValListSplit);
    }
    ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = ExperimentIndicatorExpressionItemRsEntity
        .builder()
        .experimentIndicatorExpressionItemId(experimentIndicatorExpressionItemId)
        .caseIndicatorExpressionItemId(caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId())
        .experimentId(experimentInstanceId)
        .caseId(caseInstanceId)
        .appId(appId)
        .indicatorExpressionId(experimentIndicatorExpressionId)
        .conditionRaw(caseIndicatorExpressionItemEntity.getConditionRaw())
        .conditionExpression(caseIndicatorExpressionItemEntity.getConditionExpression())
        .conditionNameList(caseIndicatorExpressionItemEntity.getConditionNameList())
        .conditionValList(experimentConditionValList)
        .resultRaw(caseIndicatorExpressionItemEntity.getResultRaw())
        .resultExpression(caseIndicatorExpressionItemEntity.getResultExpression())
        .resultNameList(caseIndicatorExpressionItemEntity.getResultNameList())
        .resultValList(experimentResultValList)
        .seq(caseIndicatorExpressionItemEntity.getSeq())
        .build();
    return experimentIndicatorExpressionItemRsEntity;
  }
}
