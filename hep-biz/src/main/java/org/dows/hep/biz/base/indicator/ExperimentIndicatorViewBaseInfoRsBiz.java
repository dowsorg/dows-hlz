package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.ExperimentIndicatorViewBaseInfoRsException;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewBaseInfoRsBiz {
  private final ExperimentIndicatorViewBaseInfoRsService experimentIndicatorViewBaseInfoRsService;
  private final ExperimentIndicatorViewBaseInfoDescrRsService experimentIndicatorViewBaseInfoDescrRsService;
  private final ExperimentIndicatorViewBaseInfoMonitorRsService experimentIndicatorViewBaseInfoMonitorRsService;
  private final ExperimentIndicatorViewBaseInfoSingleRsService experimentIndicatorViewBaseInfoSingleRsService;

  private final ExperimentTimerBiz experimentTimerBiz;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  
  public ExperimentIndicatorViewBaseInfoRsResponse get(String experimentIndicatorViewBaseInfoId, String experimentPersonId) {
    ExperimentIndicatorViewBaseInfoRsEntity experimentIndicatorViewBaseInfoRsEntity = experimentIndicatorViewBaseInfoRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewBaseInfoRsEntity::getExperimentIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method ExperimentIndicatorViewBaseInfoRsBiz.get experimentIndicatorViewBaseInfoId:{} is illegal", experimentIndicatorViewBaseInfoId);
          throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
        });
    List<ExperimentIndicatorViewBaseInfoDescrRsResponse> experimentIndicatorViewBaseInfoDescrRsResponseList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoMonitorRsResponse> experimentIndicatorViewBaseInfoMonitorRsResponseList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoSingleRsResponse> experimentIndicatorViewBaseInfoSingleRsResponseList = new ArrayList<>();
    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorInstanceIdVValMap = new HashMap<>();
    Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    String appId = experimentIndicatorViewBaseInfoRsEntity.getAppId();
    String experimentId = experimentIndicatorViewBaseInfoRsEntity.getExperimentId();
    /* runsix: TODO 等张亮那边弄好 */
//    ExperimentPeriodsResonse experimentPeriods = experimentTimerBiz.getExperimentPeriods(appId, experimentId);
//    if (Objects.isNull(experimentPeriods)) {
//      log.warn("method ExperimentIndicatorViewBaseInfoRsBiz.get experimentTimerBiz.getExperimentPeriods experimentId:{} is illegal", experimentId);
//      throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
//    }
//    Integer currentPeriod = experimentPeriods.getCurrentPeriod();
    Integer currentPeriod = 1;
    experimentIndicatorInstanceRsService.lambdaQuery()
        .select(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorName, ExperimentIndicatorInstanceRsEntity::getUnit)
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentId)
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
          .eq(ExperimentIndicatorValRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorValRsEntity::getPeriods, currentPeriod)
          .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorValRsEntity -> {
            kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
          });
    }
    List<ExperimentIndicatorViewBaseInfoDescRsEntity> experimentIndicatorViewBaseInfoDescRsEntityList = experimentIndicatorViewBaseInfoDescrRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewBaseInfoDescRsEntity::getIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
        .orderByAsc(ExperimentIndicatorViewBaseInfoDescRsEntity::getSeq)
        .list();
    experimentIndicatorViewBaseInfoDescRsEntityList.forEach(experimentIndicatorViewBaseInfoDescRsEntity -> {
      String indicatorInstanceIdArray = experimentIndicatorViewBaseInfoDescRsEntity.getIndicatorInstanceIdArray();
      List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
      populateExperimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
          kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
      experimentIndicatorViewBaseInfoDescrRsResponseList.add(ExperimentIndicatorViewBaseInfoDescrRsResponse
          .builder()
          .experimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList)
          .build());
    });
    List<ExperimentIndicatorViewBaseInfoMonitorRsEntity> experimentIndicatorViewBaseInfoMonitorRsEntityList = experimentIndicatorViewBaseInfoMonitorRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewBaseInfoMonitorRsEntity::getIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
        .orderByAsc(ExperimentIndicatorViewBaseInfoMonitorRsEntity::getSeq)
        .list();
    experimentIndicatorViewBaseInfoMonitorRsEntityList.forEach(experimentIndicatorViewBaseInfoMonitorRsEntity -> {
      List<ExperimentIndicatorViewBaseInfoMonitorContentRsResponse> experimentIndicatorViewBaseInfoMonitorContentRsResponseList = new ArrayList<>();
      String ivbimContentNameArray = experimentIndicatorViewBaseInfoMonitorRsEntity.getIvbimContentNameArray();
      List<String> contentNameList = Arrays.stream(ivbimContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
      String ivbimContentRefIndicatorInstanceIdArray = experimentIndicatorViewBaseInfoMonitorRsEntity.getIvbimContentRefIndicatorInstanceIdArray();
      List<String> indicatorInstanceIdArrayList = Arrays.stream(ivbimContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
      for (int i = 0; i <= contentNameList.size()-1; i++) {
        String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
        List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
        populateExperimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
            kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
        ExperimentIndicatorViewBaseInfoMonitorContentRsResponse experimentIndicatorViewBaseInfoMonitorContentRsResponse = ExperimentIndicatorViewBaseInfoMonitorContentRsResponse
            .builder()
            .name(contentNameList.get(i))
            .experimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList)
            .build();
        experimentIndicatorViewBaseInfoMonitorContentRsResponseList.add(experimentIndicatorViewBaseInfoMonitorContentRsResponse);
      }
      experimentIndicatorViewBaseInfoMonitorRsResponseList.add(ExperimentIndicatorViewBaseInfoMonitorRsResponse
          .builder()
          .name(experimentIndicatorViewBaseInfoMonitorRsEntity.getName())
          .experimentIndicatorViewBaseInfoMonitorContentRsResponseList(experimentIndicatorViewBaseInfoMonitorContentRsResponseList)
          .build());
    });
    List<ExperimentIndicatorViewBaseInfoSingleRsEntity> experimentIndicatorViewBaseInfoSingleRsEntityList = experimentIndicatorViewBaseInfoSingleRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewBaseInfoSingleRsEntity::getIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
        .orderByAsc(ExperimentIndicatorViewBaseInfoSingleRsEntity::getSeq)
        .list();
    experimentIndicatorViewBaseInfoSingleRsEntityList.forEach(experimentIndicatorViewBaseInfoSingleRsEntity -> {
      ExperimentIndicatorInstanceRsResponse experimentIndicatorInstanceRsResponse = populateExperimentIndicatorInstanceRsResponse(
          experimentIndicatorViewBaseInfoSingleRsEntity.getIndicatorInstanceId(), kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap
      );
      ExperimentIndicatorViewBaseInfoSingleRsResponse experimentIndicatorViewBaseInfoSingleRsResponse = ExperimentIndicatorViewBaseInfoSingleRsResponse
          .builder()
          .experimentIndicatorInstanceRsResponse(experimentIndicatorInstanceRsResponse)
          .build();
      experimentIndicatorViewBaseInfoSingleRsResponseList.add(experimentIndicatorViewBaseInfoSingleRsResponse);
    });
    return ExperimentIndicatorViewBaseInfoRsResponse
        .builder()
        .experimentIndicatorViewBaseInfoDescrRsResponseList(experimentIndicatorViewBaseInfoDescrRsResponseList)
        .experimentIndicatorViewBaseInfoMonitorRsResponseList(experimentIndicatorViewBaseInfoMonitorRsResponseList)
        .experimentIndicatorViewBaseInfoSingleRsResponseList(experimentIndicatorViewBaseInfoSingleRsResponseList)
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
}
