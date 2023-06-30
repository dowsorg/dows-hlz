package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.ExperimentIndicatorViewBaseInfoRsException;
import org.dows.hep.api.exception.ExperimentIndicatorViewMonitorFollowupReportRsException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewMonitorFollowupReportRsBiz {
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorViewMonitorFollowupRsService experimentIndicatorViewMonitorFollowupRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;

  private final ExperimentIndicatorViewMonitorFollowupPlanRsService experimentIndicatorViewMonitorFollowupPlanRsService;

  private final ExperimentIndicatorViewMonitorFollowupReportRsService experimentIndicatorViewMonitorFollowupReportRsService;

  public static ExperimentIndicatorViewMonitorFollowupPlanRsResponse experimentIndicatorViewMonitorFollowupPlanRs2Response(ExperimentIndicatorViewMonitorFollowupPlanRsEntity experimentIndicatorViewMonitorFollowupPlanRsEntity) {
    if (Objects.isNull(experimentIndicatorViewMonitorFollowupPlanRsEntity)) {
      return null;
    };
    return ExperimentIndicatorViewMonitorFollowupPlanRsResponse
        .builder()
        .experimentIndicatorViewMonitorFollowupPlanId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentIndicatorViewMonitorFollowupPlanId())
        .experimentId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentId())
        .caseId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getCaseId())
        .appId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getAppId())
        .period(experimentIndicatorViewMonitorFollowupPlanRsEntity.getPeriod())
        .indicatorFuncId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getIndicatorFuncId())
        .experimentPersonId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentPersonId())
        .operateFlowId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getOperateFlowId())
        .intervalDay(experimentIndicatorViewMonitorFollowupPlanRsEntity.getIntervalDay())
        .experimentIndicatorViewMonitorFollowupPlanId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentIndicatorViewMonitorFollowupId())
        .deleted(experimentIndicatorViewMonitorFollowupPlanRsEntity.getDeleted())
        .dt(experimentIndicatorViewMonitorFollowupPlanRsEntity.getDt())
        .build();
  }

  @Transactional(rollbackFor = Exception.class)
  public void monitorFollowupCheck(ExperimentMonitorFollowupCheckRequestRs experimentMonitorFollowupCheckRequestRs) {
    ExperimentIndicatorViewMonitorFollowupReportRsEntity experimentIndicatorViewMonitorFollowupReportRsEntity = null;
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer period = 1;
    String experimentPersonId = experimentMonitorFollowupCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentMonitorFollowupCheckRequestRs.getIndicatorFuncId();
    String appId = experimentMonitorFollowupCheckRequestRs.getAppId();
    String experimentId = experimentMonitorFollowupCheckRequestRs.getExperimentId();
    String indicatorViewMonitorFollowupId = experimentMonitorFollowupCheckRequestRs.getIndicatorViewMonitorFollowupId();
    ExperimentIndicatorViewMonitorFollowupRsEntity experimentIndicatorViewMonitorFollowupRsEntity = experimentIndicatorViewMonitorFollowupRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewMonitorFollowupRsEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("ExperimentIndicatorViewMonitorFollowupReportRsBiz.monitorFollowupCheck param ExperimentMonitorFollowupCheckRequestRs indicatorViewMonitorFollowupId:{} is illegal", indicatorViewMonitorFollowupId);
          throw new ExperimentIndicatorViewMonitorFollowupReportRsException(EnumESC.VALIDATE_EXCEPTION);
        });
    String caseId = experimentIndicatorViewMonitorFollowupRsEntity.getCaseId();
    String name = experimentIndicatorViewMonitorFollowupRsEntity.getName();
    String ivmfContentNameArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentNameArray();
    String ivmfContentRefIndicatorInstanceIdArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentRefIndicatorInstanceIdArray();
    AtomicInteger atomicIntegerCount = new AtomicInteger(1);
    ExperimentIndicatorViewMonitorFollowupReportRsEntity lastExperimentIndicatorViewMonitorFollowupReportRsEntity = experimentIndicatorViewMonitorFollowupReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod, period)
        .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
        .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentPersonId, experimentPersonId)
        .orderByDesc(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getCount)
        .last(EnumString.LIMIT_1.getStr())
        .one();
    if (Objects.nonNull(lastExperimentIndicatorViewMonitorFollowupReportRsEntity)) {
      Integer count = lastExperimentIndicatorViewMonitorFollowupReportRsEntity.getCount();
      atomicIntegerCount.set(count+1);
    }

    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorInstanceIdVValMap = new HashMap<>();
    Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    experimentIndicatorInstanceRsService.lambdaQuery()
        .select(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorName, ExperimentIndicatorInstanceRsEntity::getUnit)
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          kInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(
              experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
        });
    if (!experimentIndicatorInstanceIdSet.isEmpty()) {
      experimentIndicatorValRsService.lambdaQuery()
          .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
          .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorValRsEntity -> {
            kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
          });
    }
    String indicatorCurrentValArray = null;
    List<String> indicatorCurrentValArrayList = new ArrayList<>();
    List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
    List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
    for (int i = 0; i <= contentNameList.size()-1; i++) {
      String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
      List<String> indicatorInstanceIdList = Arrays.stream(indicatorInstanceIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
      List<String> indicatorCurrentValList = new ArrayList<>();
      indicatorInstanceIdList.forEach(indicatorInstanceId -> {
        String experimentIndicatorInstanceId = kInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        String indicatorCurrentVal = kExperimentIndicatorInstanceIdVValMap.get(experimentIndicatorInstanceId);
        indicatorCurrentValList.add(indicatorCurrentVal);
      });
      String indicatorCurrentValArrayInside = String.join(EnumString.COMMA.getStr(), indicatorCurrentValList);
      indicatorCurrentValArrayList.add(indicatorCurrentValArrayInside);
    }
    indicatorCurrentValArray = String.join(EnumString.JIN.getStr(), indicatorCurrentValArrayList);
    /* runsix:TODO 等吴治霖弄好 */
    String operateFlowId = "1";
    ExperimentIndicatorViewMonitorFollowupReportRsEntity
        .builder()
        .experimentIndicatorViewMonitorFollowupReportId(idGenerator.nextIdStr())
        .experimentId(experimentId)
        .caseId(caseId)
        .appId(appId)
        .period(period)
        .indicatorFuncId(indicatorFuncId)
        .experimentPersonId(experimentPersonId)
        .operateFlowId(operateFlowId)
        .count(atomicIntegerCount.get())
        .experimentIndicatorViewMonitorFollowupId(indicatorViewMonitorFollowupId)
        .name(name)
        .ivmfContentNameArray(ivmfContentNameArray)
        .ivmfContentRefIndicatorInstanceIdArray(ivmfContentRefIndicatorInstanceIdArray)
        .ivmfIndicatorCurrentValArray(indicatorCurrentValArray)
        .build();
    experimentIndicatorViewMonitorFollowupReportRsService.saveOrUpdate(experimentIndicatorViewMonitorFollowupReportRsEntity);
  }

  /**
   * runsix method process
   * 1.plan does not exist
   *   1.1 get experimentIndicatorViewMonitorFollowupRsResponseList
   * 2.plan exist
   *   1.2 return last experimentIndicatorViewMonitorFollowupReportRsResponse
  */
  public ExperimentMonitorFollowupRsResponse get(String indicatorFuncId, String experimentPersonId) {
    /* runsix:TODO 这个等张亮的期数弄好再调整 */
    Integer period = 1;
    ExperimentIndicatorViewMonitorFollowupPlanRsResponse experimentIndicatorViewMonitorFollowupPlanRsResponse = null;
    List<ExperimentIndicatorViewMonitorFollowupRsResponse> experimentIndicatorViewMonitorFollowupRsResponseList = new ArrayList<>();
    ExperimentIndicatorViewMonitorFollowupReportRsResponse experimentIndicatorViewMonitorFollowupReportRsResponse = null;
    ExperimentIndicatorViewMonitorFollowupPlanRsEntity experimentIndicatorViewMonitorFollowupPlanRsEntity = experimentIndicatorViewMonitorFollowupPlanRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getPeriod, period)
        .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getExperimentPersonId, experimentPersonId)
        .one();
    experimentIndicatorViewMonitorFollowupPlanRsResponse = experimentIndicatorViewMonitorFollowupPlanRs2Response(experimentIndicatorViewMonitorFollowupPlanRsEntity);
    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorInstanceIdVValMap = new HashMap<>();
    Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    experimentIndicatorInstanceRsService.lambdaQuery()
        .select(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorName, ExperimentIndicatorInstanceRsEntity::getUnit)
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          kInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(
              experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
        });
    if (!experimentIndicatorInstanceIdSet.isEmpty()) {
      experimentIndicatorValRsService.lambdaQuery()
          .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
          .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorValRsEntity -> {
            kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
          });
    }
    experimentIndicatorViewMonitorFollowupRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewMonitorFollowupRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorViewMonitorFollowupRsEntity -> {
          List<ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse> experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList = new ArrayList<>();
          String ivmfContentNameArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentNameArray();
          List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String ivmfContentRefIndicatorInstanceIdArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentRefIndicatorInstanceIdArray();
          List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
          for (int i = 0; i <= contentNameList.size()-1; i++) {
            String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
            List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
            populateExperimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
                kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
            ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse experimentIndicatorViewMonitorFollowupFollowupContentRsResponse = ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse
                .builder()
                .name(contentNameList.get(i))
                .experimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList)
                .build();
            experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList.add(experimentIndicatorViewMonitorFollowupFollowupContentRsResponse);
          }
          ExperimentIndicatorViewMonitorFollowupRsResponse experimentIndicatorViewMonitorFollowupRsResponse = ExperimentIndicatorViewMonitorFollowupRsResponse
              .builder()
              .experimentIndicatorViewMonitorFollowupId(experimentIndicatorViewMonitorFollowupRsEntity.getExperimentIndicatorViewMonitorFollowupId())
              .name(experimentIndicatorViewMonitorFollowupRsEntity.getName())
              .experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList(experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList)
              .build();
          experimentIndicatorViewMonitorFollowupRsResponseList.add(experimentIndicatorViewMonitorFollowupRsResponse);
        });
    if (Objects.isNull(experimentIndicatorViewMonitorFollowupPlanRsEntity)) {
    } else {
      ExperimentIndicatorViewMonitorFollowupReportRsEntity experimentIndicatorViewMonitorFollowupReportRsEntity = experimentIndicatorViewMonitorFollowupReportRsService.lambdaQuery()
          .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod, period)
          .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentPersonId, experimentPersonId)
          .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getIndicatorFuncId, indicatorFuncId)
          .orderByDesc(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getCount)
          .last(EnumString.LIMIT_1.getStr())
          .one();
      if (Objects.isNull(experimentIndicatorViewMonitorFollowupReportRsEntity)) {
        experimentIndicatorViewMonitorFollowupReportRsResponse = ExperimentIndicatorViewMonitorFollowupReportRsResponse
            .builder()
            .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
            .build();
      } else {
        List<ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse> experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList = new ArrayList<>();
        String ivmfContentNameArray = experimentIndicatorViewMonitorFollowupReportRsEntity.getIvmfContentNameArray();
        List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
        String ivmfContentRefIndicatorInstanceIdArray = experimentIndicatorViewMonitorFollowupReportRsEntity.getIvmfContentRefIndicatorInstanceIdArray();
        List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
        String ivmfIndicatorCurrentValArray = experimentIndicatorViewMonitorFollowupReportRsEntity.getIvmfIndicatorCurrentValArray();
        List<String> indicatorInstanceCurrentValArrayList = Arrays.stream(ivmfIndicatorCurrentValArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
        for (int i = 0; i <= contentNameList.size()-1; i++) {
          String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
          String indicatorCurrentValArray = indicatorInstanceCurrentValArrayList.get(i);
          List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
          populateExperimentIndicatorInstanceRsResponseListMF(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
              kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, indicatorCurrentValArray);
          ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse experimentIndicatorViewMonitorFollowupFollowupContentRsResponse = ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse
              .builder()
              .name(contentNameList.get(i))
              .experimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList)
              .build();
          experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList.add(experimentIndicatorViewMonitorFollowupFollowupContentRsResponse);
        }
        ExperimentIndicatorViewMonitorFollowupRsResponse experimentIndicatorViewMonitorFollowupRsResponse = ExperimentIndicatorViewMonitorFollowupRsResponse
            .builder()
            .experimentIndicatorViewMonitorFollowupId(experimentIndicatorViewMonitorFollowupReportRsEntity.getExperimentIndicatorViewMonitorFollowupId())
            .name(experimentIndicatorViewMonitorFollowupReportRsEntity.getName())
            .experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList(experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList)
            .build();
        experimentIndicatorViewMonitorFollowupRsResponseList.add(experimentIndicatorViewMonitorFollowupRsResponse);
        experimentIndicatorViewMonitorFollowupReportRsResponse = ExperimentIndicatorViewMonitorFollowupReportRsResponse
            .builder()
            .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
            .build();
      }
    }
    return ExperimentMonitorFollowupRsResponse
        .builder()
        .experimentIndicatorViewMonitorFollowupPlanRsResponse(experimentIndicatorViewMonitorFollowupPlanRsResponse)
        .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
        .experimentIndicatorViewMonitorFollowupReportRsResponse(experimentIndicatorViewMonitorFollowupReportRsResponse)
        .build();
  }

  public void populateExperimentIndicatorInstanceRsResponseList(
      List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList,
      String indicatorInstanceIdArray,
      Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
      Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
      Map<String, String> kExperimentIndicatorInstanceIdVValMap
  ) {
    List<String> indicatorInstanceIdList = Arrays.stream(indicatorInstanceIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
    indicatorInstanceIdList.forEach(indicatorInstanceId -> {
      ExperimentIndicatorInstanceRsResponse experimentIndicatorInstanceRsResponse = populateExperimentIndicatorInstanceRsResponse(
          indicatorInstanceId, kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
      experimentIndicatorInstanceRsResponseList.add(experimentIndicatorInstanceRsResponse);
    });
  }

  public ExperimentIndicatorInstanceRsResponse populateExperimentIndicatorInstanceRsResponse(
      String indicatorInstanceId,
      Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
      Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
      Map<String, String> kExperimentIndicatorInstanceIdVValMap) {
    String experimentIndicatorInstanceId = kInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(experimentIndicatorInstanceId);
    if (Objects.isNull(experimentIndicatorInstanceRsEntity)) {
      log.warn("method ExperimentIndicatorViewBaseInfoRsBiz.get populateExperimentIndicatorInstanceRsResponse indicatorInstanceId:{} is illegal, mapped no experimentIndicatorInstanceRsEntity", indicatorInstanceId);
      throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
    }
    return ExperimentIndicatorInstanceRsResponse.getExperimentIndicatorInstanceRsResponse(
        experimentIndicatorInstanceRsEntity.getIndicatorName(), kExperimentIndicatorInstanceIdVValMap.get(experimentIndicatorInstanceId), experimentIndicatorInstanceRsEntity.getUnit()
    );
  }

  public void populateExperimentIndicatorInstanceRsResponseListMF(
      List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList,
      String indicatorInstanceIdArray,
      Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
      Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
      String indicatorCurrentValArray
  ) {
    List<String> indicatorInstanceIdList = Arrays.stream(indicatorInstanceIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
    List<String> indicatorCurrentValList = Arrays.stream(indicatorCurrentValArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
    for (int i = 0; i <= indicatorInstanceIdList.size()-1; i++) {
      String indicatorInstanceId = indicatorInstanceIdList.get(i);
      String indicatorCurrentVal = indicatorCurrentValList.get(i);
      ExperimentIndicatorInstanceRsResponse experimentIndicatorInstanceRsResponse = populateExperimentIndicatorInstanceRsResponseMF(
          indicatorInstanceId, kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, indicatorCurrentVal);
      experimentIndicatorInstanceRsResponseList.add(experimentIndicatorInstanceRsResponse);
    }
  }

  public ExperimentIndicatorInstanceRsResponse populateExperimentIndicatorInstanceRsResponseMF(
      String indicatorInstanceId,
      Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
      Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
      String currentVal) {
    String experimentIndicatorInstanceId = kInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(experimentIndicatorInstanceId);
    if (Objects.isNull(experimentIndicatorInstanceRsEntity)) {
      log.warn("method ExperimentIndicatorViewBaseInfoRsBiz.get populateExperimentIndicatorInstanceRsResponse indicatorInstanceId:{} is illegal, mapped no experimentIndicatorInstanceRsEntity", indicatorInstanceId);
      throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
    }
    return ExperimentIndicatorInstanceRsResponse.getExperimentIndicatorInstanceRsResponse(
        experimentIndicatorInstanceRsEntity.getIndicatorName(), currentVal, experimentIndicatorInstanceRsEntity.getUnit()
    );
  }
}
