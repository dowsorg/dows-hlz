package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CopyViewIndicatorRequestRs;
import org.dows.hep.api.base.indicator.request.JudgeViewIndicatorRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.RsCopyException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
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
  private final IdGenerator idGenerator;
  private final CaseInstanceService caseInstanceService;
  private final CaseOrgService caseOrgService;
  private final CaseOrgModuleService caseOrgModuleService;
  private final CaseOrgModuleFuncRefService caseOrgModuleFuncRefService;
  private final IndicatorFuncService indicatorFuncService;
  private final IndicatorInstanceService indicatorInstanceService;
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
      ExperimentIndicatorViewBaseInfoRsEntity experimentIndicatorViewBaseInfoRsEntity = ExperimentIndicatorViewBaseInfoRsEntity
          .builder()
          .experimentIndicatorViewBaseInfoId(experimentIndicatorViewBaseInfoId)
          .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
          .experimentId(experimentInstanceId)
          .caseId(caseInstanceId)
          .appId(appId)
          .indicatorFuncId(idGenerator.nextIdStr())
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
              .indicatorInstanceNameArray(indicatorInstanceNameArray)
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
              .ivbimContentRefIndicatorInstanceNameArray(ivbimContentRefIndicatorInstanceNameArray)
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
              .indicatorInstanceName(indicatorInstanceName)
              .seq(indicatorViewBaseInfoSingleEntity.getSeq())
              .build();
          experimentIndicatorViewBaseInfoSingleRsEntityList.add(experimentIndicatorViewBaseInfoSingleRsEntity);
        });
      }
      /* runsix:查看指标-基本信息-单一指标表 */
    });
    /* runsix:查看指标-基本信息 */

    experimentIndicatorViewBaseInfoRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoRsEntityList);
    experimentIndicatorViewBaseInfoDescrRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoDescRsEntityList);
    experimentIndicatorViewBaseInfoMonitorRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoMonitorRsEntityList);
    experimentIndicatorViewBaseInfoSingleRsService.saveOrUpdateBatch(experimentIndicatorViewBaseInfoSingleRsEntityList);
  }

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyJudgeIndicator(JudgeViewIndicatorRequestRs judgeViewIndicatorRequestRs) {

  }
}
