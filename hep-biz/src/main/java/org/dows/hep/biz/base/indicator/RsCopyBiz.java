package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CopyViewIndicatorRequestRs;
import org.dows.hep.api.base.indicator.request.JudgeViewIndicatorRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.exception.RsCopyException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCopyBiz {
  private final CaseInstanceService caseInstanceService;
  private final CaseOrgService caseOrgService;
  private final CaseOrgModuleService caseOrgModuleService;
  private final CaseOrgModuleFuncRefService caseOrgModuleFuncRefService;
  private final IndicatorFuncService indicatorFuncService;
  private final IndicatorInstanceService indicatorInstanceService;
  /* runsix:view */
  private final IndicatorViewBaseInfoService indicatorViewBaseInfoService;
  private final IndicatorViewBaseInfoDescrService indicatorViewBaseInfoDescrService;
  private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;
  private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
  private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
  private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;
  private final IndicatorViewBaseInfoSingleService indicatorViewBaseInfoSingleService;
  private final IndicatorViewMonitorFollowupService indicatorViewMonitorFollowupService;
  private final IndicatorViewMonitorFollowupFollowupContentService indicatorViewMonitorFollowupFollowupContentService;
  private final IndicatorViewMonitorFollowupContentRefService indicatorViewMonitorFollowupContentRefService;
  private final IndicatorViewPhysicalExamService indicatorViewPhysicalExamService;
  private final IndicatorViewSupportExamService indicatorViewSupportExamService;
  /* runsix:todo wuzhilin */
  /* runsix:judge */
  private final IndicatorJudgeRiskFactorService indicatorJudgeRiskFactorService;
  private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;
  private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;
  private final IndicatorJudgeDiseaseProblemService indicatorJudgeDiseaseProblemService;

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyViewIndicator(CopyViewIndicatorRequestRs copyViewIndicatorRequestRs) {
    String appId = copyViewIndicatorRequestRs.getAppId();
    String caseInstanceId = copyViewIndicatorRequestRs.getCaseInstanceId();
    String experimentInstanceId = copyViewIndicatorRequestRs.getExperimentInstanceId();
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
    caseOrgModuleService.lambdaQuery()
        .eq(CaseOrgModuleEntity::getAppId, appId)
        .in(CaseOrgModuleEntity::getCaseOrgId, caseOrgIdSet)
        .list()
        .forEach(caseOrgModuleEntity -> {
          kCaseOrgModuleIdVCaseOrgModuleEntityMap.put(caseOrgModuleEntity.getCaseOrgModuleId(), caseOrgModuleEntity);
        });
    Set<String> caseOrgModuleIdSet = kCaseOrgModuleIdVCaseOrgModuleEntityMap.keySet();
    if (caseOrgModuleIdSet.isEmpty()) {
      log.warn("method RsCopyBiz.rsCopyViewIndicator has no caseOrgModule");
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
    Set<String> indicatorFuncIdSet = new HashSet<>();
    caseOrgModuleFuncRefService.lambdaQuery()
        .eq(CaseOrgModuleFuncRefEntity::getAppId, appId)
        .in(CaseOrgModuleFuncRefEntity::getCaseOrgModuleId, caseOrgModuleIdSet)
        .list()
        .forEach(caseOrgModuleFuncRefEntity -> {
          indicatorFuncIdSet.add(caseOrgModuleFuncRefEntity.getIndicatorFuncId());
        });
    if (indicatorFuncIdSet.isEmpty()) {
      log.warn("method RsCopyBiz.rsCopyViewIndicator has no indicatorFunc");
      throw new RsCopyException(EnumESC.VALIDATE_EXCEPTION);
    }
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
    Map<String, IndicatorViewBaseInfoEntity> kBaseInfoIdVBaseInfoMap = new HashMap<>();
    Set<String> baseInfoIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoDescrEntity> kBaseInfoDescrIdVBaseInfoDescrEntityMap = new HashMap<>();
    Set<String> baseInfoDescrIdSet = new HashSet<>();
    Map<String, List<String>> kBaseInfoDescrIdVInstanceIdListMap = new HashMap<>();
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
            baseInfoDescrIdSet.add(indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId());
            kBaseInfoDescrIdVBaseInfoDescrEntityMap.put(indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId(), indicatorViewBaseInfoDescrEntity);
          });
    }
    if (!baseInfoDescrIdSet.isEmpty()) {
      indicatorViewBaseInfoDescrRefService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescId, baseInfoDescrIdSet)
          .list()
          .forEach(indicatorViewBaseInfoDescrRefEntity -> {
            String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId();
            String indicatorInstanceId = indicatorViewBaseInfoDescrRefEntity.getIndicatorInstanceId();
            List<String> indicatorInstanceIdList = kBaseInfoDescrIdVInstanceIdListMap.get(indicatorViewBaseInfoDescId);
            if (Objects.isNull(indicatorInstanceIdList)) {
              indicatorInstanceIdList = new ArrayList<>();
            }
            indicatorInstanceIdList.add(indicatorInstanceId);
            kBaseInfoDescrIdVInstanceIdListMap.put(indicatorViewBaseInfoDescId, indicatorInstanceIdList);
          });
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyJudgeIndicator(JudgeViewIndicatorRequestRs judgeViewIndicatorRequestRs) {

  }
}
