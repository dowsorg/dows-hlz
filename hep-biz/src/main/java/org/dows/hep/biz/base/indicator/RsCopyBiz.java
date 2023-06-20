package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.RsCopyExperimentRequestRs;
import org.dows.hep.api.base.indicator.request.JudgeViewIndicatorRequestRs;
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
import java.util.concurrent.atomic.AtomicInteger;
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
  private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;
  private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;
  private final IndicatorJudgeDiseaseProblemService indicatorJudgeDiseaseProblemService;

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyExperiment(RsCopyExperimentRequestRs rsCopyExperimentRequestRs) {
    List<ExperimentOrgModuleRsEntity> experimentOrgModuleRsEntityList = new ArrayList<>();
    List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewPhysicalExamRsEntity> experimentIndicatorViewPhysicalExamRsEntityList = new ArrayList<>();
    List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList = new ArrayList<>();
    List<ExperimentIndicatorViewSupportExamRsEntity> experimentIndicatorViewSupportExamRsEntityList = new ArrayList<>();
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
          String indicatorInstanceName = null;
          IndicatorInstanceEntity indicatorInstanceEntity = kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorViewBaseInfoSingleEntity.getIndicatorInstanceId());
          if (Objects.nonNull(indicatorInstanceEntity)) {
            indicatorInstanceId = indicatorInstanceEntity.getIndicatorInstanceId();
            indicatorInstanceName = indicatorInstanceEntity.getIndicatorName();
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
    if (!funcIndicatorViewPhysicalExamIdSet.isEmpty()) {
      indicatorViewPhysicalExamEntityList.addAll(indicatorViewPhysicalExamService.lambdaQuery()
          .eq(IndicatorViewPhysicalExamEntity::getAppId, appId)
          .in(IndicatorViewPhysicalExamEntity::getIndicatorFuncId, funcIndicatorViewPhysicalExamIdSet)
          .list());
    }
    if (!indicatorViewPhysicalExamEntityList.isEmpty()) {
      indicatorViewPhysicalExamEntityList.forEach(indicatorViewPhysicalExamEntity -> {
        String experimentIndicatorViewPhysicalExamId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorViewPhysicalExamEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name1 = indicatorFuncEntity.getName();
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(experimentIndicatorViewPhysicalExamId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            experimentIndicatorFuncIdList.add(experimentIndicatorViewPhysicalExamId);
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(experimentIndicatorViewPhysicalExamId, EnumIndicatorCategory.VIEW_MANAGEMENT_NO_REPORT_TWO_LEVEL.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(experimentIndicatorViewPhysicalExamId, name1);
          });
        }
        String indicatorViewPhysicalExamId = indicatorViewPhysicalExamEntity.getIndicatorViewPhysicalExamId();
        String name = indicatorViewPhysicalExamEntity.getName();
        BigDecimal fee = indicatorViewPhysicalExamEntity.getFee();
        String indicatorInstanceId = indicatorViewPhysicalExamEntity.getIndicatorInstanceId();
        String resultAnalysis = indicatorViewPhysicalExamEntity.getResultAnalysis();
        Integer status = indicatorViewPhysicalExamEntity.getStatus();
        ExperimentIndicatorViewPhysicalExamRsEntity experimentIndicatorViewPhysicalExamRsEntity = ExperimentIndicatorViewPhysicalExamRsEntity
            .builder()
            .experimentIndicatorViewPhysicalExamId(experimentIndicatorViewPhysicalExamId)
            .indicatorViewPhysicalExamId(indicatorViewPhysicalExamId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .name(name)
            .fee(fee)
            .indicatorInstanceId(indicatorInstanceId)
            .resultAnalysis(resultAnalysis)
            .status(status)
            .build();
        experimentIndicatorViewPhysicalExamRsEntityList.add(experimentIndicatorViewPhysicalExamRsEntity);
      });
    }
    /* runsix:查看指标-体格检查-二级类-无报告 */
    /* runsix:查看指标-辅助检查-四级类-无报告 */
    if (!funcIndicatorViewSupportExamIdSet.isEmpty()) {
      indicatorViewSupportExamEntityList.addAll(indicatorViewSupportExamService.lambdaQuery()
          .eq(IndicatorViewSupportExamEntity::getAppId, appId)
          .in(IndicatorViewSupportExamEntity::getIndicatorFuncId, funcIndicatorViewSupportExamIdSet)
          .list());
    }
    if (!indicatorViewSupportExamEntityList.isEmpty()) {
      indicatorViewSupportExamEntityList.forEach(indicatorViewSupportExamEntity -> {
        String experimentIndicatorViewSupportExamId = idGenerator.nextIdStr();
        String indicatorFuncId = indicatorViewSupportExamEntity.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = kIndicatorFuncIdVIndicatorFuncEntityMap.get(indicatorFuncId);
        String name1 = indicatorFuncEntity.getName();
        kExperimentIndicatorFuncIdVIndicatorFuncIdMap.put(experimentIndicatorViewSupportExamId, indicatorFuncId);
        List<String> caseOrgModuleIdList = kIndicatorFuncIdVCaseOrgModuleIdList.get(indicatorFuncId);
        if (Objects.nonNull(caseOrgModuleIdList)) {
          caseOrgModuleIdList.forEach(caseOrgModuleId -> {
            List<String> experimentIndicatorFuncIdList = kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.get(caseOrgModuleId);
            if (Objects.isNull(experimentIndicatorFuncIdList)) {
              experimentIndicatorFuncIdList = new ArrayList<>();
            }
            experimentIndicatorFuncIdList.add(experimentIndicatorViewSupportExamId);
            kCaseOrgModuleIdVExperimentIndicatorFuncIdListMap.put(caseOrgModuleId, experimentIndicatorFuncIdList);
            kExperimentIndicatorFuncIdVIndicatorCategoryIdMap.put(experimentIndicatorViewSupportExamId, EnumIndicatorCategory.VIEW_MANAGEMENT_NO_REPORT_TWO_LEVEL.getCode());
            kExperimentIndicatorFuncIdVIndicatorFuncNameMap.put(experimentIndicatorViewSupportExamId, name1);
          });
        }
        String indicatorViewSupportExamId = indicatorViewSupportExamEntity.getIndicatorViewSupportExamId();
        String name = indicatorViewSupportExamEntity.getName();
        BigDecimal fee = indicatorViewSupportExamEntity.getFee();
        String indicatorInstanceId = indicatorViewSupportExamEntity.getIndicatorInstanceId();
        String resultAnalysis = indicatorViewSupportExamEntity.getResultAnalysis();
        Integer status = indicatorViewSupportExamEntity.getStatus();
        ExperimentIndicatorViewSupportExamRsEntity experimentIndicatorViewSupportExamRsEntity = ExperimentIndicatorViewSupportExamRsEntity
            .builder()
            .experimentIndicatorViewSupportExamId(experimentIndicatorViewSupportExamId)
            .indicatorViewSupportExamId(indicatorViewSupportExamId)
            .experimentId(experimentInstanceId)
            .caseId(caseInstanceId)
            .appId(appId)
            .name(name)
            .fee(fee)
            .indicatorInstanceId(indicatorInstanceId)
            .resultAnalysis(resultAnalysis)
            .status(status)
            .build();
        experimentIndicatorViewSupportExamRsEntityList.add(experimentIndicatorViewSupportExamRsEntity);
      });
    }
    /* runsix:查看指标-辅助检查-四级类-无报告 */
    /* runsix:TODO 判断指标-危险因素-二级类-无报告（有公式） */
    /* runsix:TODO 判断指标-危险因素-二级类-无报告（有公式） */
    /* runsix:判断指标-健康问题-三级类-无报告（无公式） */
    /* runsix:判断指标-健康问题-三级类-无报告（无公式） */
    /* runsix:判断指标-健康指导-二级类-有报告（无公式） */
    /* runsix:判断指标-健康指导-二级类-有报告（无公式） */
    /* runsix:判断指标-疾病问题-四级类-无报告（无公式） */
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
        String min = null;
        String max = null;
        String def = null;
        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
        if (Objects.nonNull(caseIndicatorRuleEntity)) {
          min = caseIndicatorRuleEntity.getMin();
          max = caseIndicatorRuleEntity.getMax();
          def = caseIndicatorRuleEntity.getDef();
        }
        experimentIndicatorInstanceRsEntityList.add(
            ExperimentIndicatorInstanceRsEntity
                .builder()
                .experimentIndicatorInstanceId(idGenerator.nextIdStr())
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
    experimentIndicatorInstanceRsService.saveOrUpdateBatch(experimentIndicatorInstanceRsEntityList);
    experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
  }
}
