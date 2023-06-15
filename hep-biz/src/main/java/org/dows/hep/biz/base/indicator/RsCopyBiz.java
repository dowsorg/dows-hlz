package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CopyViewIndicatorRequestRs;
import org.dows.hep.api.base.indicator.request.JudgeViewIndicatorRequestRs;
import org.dows.hep.api.enums.EnumESC;
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
    indicatorFuncService.lambdaQuery()
        .eq(IndicatorFuncEntity::getAppId, appId)
        .in(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncIdSet)
        .list()
  }

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyJudgeIndicator(JudgeViewIndicatorRequestRs judgeViewIndicatorRequestRs) {

  }
}
