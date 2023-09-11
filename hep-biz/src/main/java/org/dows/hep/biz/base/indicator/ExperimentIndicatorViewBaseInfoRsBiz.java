package org.dows.hep.biz.base.indicator;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.GetIndicatorBaseInfo;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.ExperimentIndicatorViewBaseInfoRsException;
import org.dows.hep.biz.eval.QueryPersonBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
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
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;

  private final IndicatorFuncService indicatorFuncService;

  private final IndicatorViewBaseInfoService indicatorViewBaseInfoService;

  private final QueryPersonBiz queryPersonBiz;

  public ExperimentIndicatorViewBaseInfoRsResponse getBaseInfo(GetIndicatorBaseInfo getIndicatorBaseInfo) throws ExecutionException, InterruptedException {
    IndicatorFuncEntity indicatorFuncEntity = indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getIndicatorCategoryId, EnumIndicatorCategory.VIEW_MANAGEMENT_BASE_INFO.getCode())
            .eq(IndicatorFuncEntity::getSeq,2)
            //.eq(IndicatorFuncEntity::getAppId, getIndicatorBaseInfo.getAppId()))
            .oneOpt()
            .orElse(null);
    if(indicatorFuncEntity == null){
      return null;
    }
    IndicatorViewBaseInfoEntity indicatorViewBaseInfoEntity = indicatorViewBaseInfoService.lambdaQuery()
            //.eq(IndicatorViewBaseInfoEntity::getAppId, appId)
            .eq(IndicatorViewBaseInfoEntity::getIndicatorFuncId, indicatorFuncEntity.getIndicatorFuncId())
            .oneOpt()
            .orElse(null);
    if(indicatorViewBaseInfoEntity == null){
      return null;
    }
    ExperimentIndicatorViewBaseInfoRsEntity experimentIndicatorViewBaseInfoRsEntity = experimentIndicatorViewBaseInfoRsService.lambdaQuery()
            .eq(ExperimentIndicatorViewBaseInfoRsEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoEntity.getIndicatorViewBaseInfoId())
            //.eq(ExperimentIndicatorViewBaseInfoRsEntity::getAppId, appId)
            .eq(ExperimentIndicatorViewBaseInfoRsEntity::getExperimentId, getIndicatorBaseInfo.getExperimentId())
            .oneOpt()
            .orElse(null);
    if(experimentIndicatorViewBaseInfoRsEntity == null){
      return null;
    }

    String experimentIndicatorViewBaseInfoId = experimentIndicatorViewBaseInfoRsEntity.getExperimentIndicatorViewBaseInfoId();
      ExperimentIndicatorViewBaseInfoRsResponse experimentIndicatorViewBaseInfoRsResponse =
              get(null, experimentIndicatorViewBaseInfoId, getIndicatorBaseInfo.getExperimentPersonId(), getIndicatorBaseInfo.getPeriods());
      return experimentIndicatorViewBaseInfoRsResponse;

  }


  public ExperimentIndicatorViewBaseInfoRsResponse get(String experimentId,String experimentIndicatorViewBaseInfoId, String experimentPersonId, Integer periods) throws ExecutionException, InterruptedException {
//    String experimentId = null;
    if(StrUtil.isNotBlank(experimentIndicatorViewBaseInfoId)) {
      ExperimentIndicatorViewBaseInfoRsEntity experimentIndicatorViewBaseInfoRsEntity = experimentIndicatorViewBaseInfoRsService.lambdaQuery()
              .eq(ExperimentIndicatorViewBaseInfoRsEntity::getExperimentIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
              .oneOpt()
              .orElseThrow(() -> {
                log.warn("method ExperimentIndicatorViewBaseInfoRsBiz.get experimentIndicatorViewBaseInfoId:{} is illegal", experimentIndicatorViewBaseInfoId);
                throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
              });
      String appId = experimentIndicatorViewBaseInfoRsEntity.getAppId();
      experimentId = experimentIndicatorViewBaseInfoRsEntity.getExperimentId();
    }
    List<ExperimentIndicatorViewBaseInfoDescrRsResponse> experimentIndicatorViewBaseInfoDescrRsResponseList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoMonitorRsResponse> experimentIndicatorViewBaseInfoMonitorRsResponseList = new ArrayList<>();
    List<ExperimentIndicatorViewBaseInfoSingleRsResponse> experimentIndicatorViewBaseInfoSingleRsResponseList = new ArrayList<>();
    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    Map<String, String> kExperimentIndicatorInstanceIdVValMap = new HashMap<>();
    Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();

    experimentIndicatorInstanceRsService.lambdaQuery()
        .select(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorName, ExperimentIndicatorInstanceRsEntity::getUnit)
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          kInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(
              experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
        });

    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap =queryPersonBiz.populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(experimentPersonId,periods);

    kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.forEach((experimentIndicatorInstanceId, experimentIndicatorValRsEntity) -> {
      kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorInstanceId, experimentIndicatorValRsEntity.getCurrentVal());
    });

    List<ExperimentIndicatorViewBaseInfoDescRsEntity> experimentIndicatorViewBaseInfoDescRsEntityList = experimentIndicatorViewBaseInfoDescrRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewBaseInfoDescRsEntity::getIndicatorViewBaseInfoId, experimentIndicatorViewBaseInfoId)
        .orderByAsc(ExperimentIndicatorViewBaseInfoDescRsEntity::getSeq)
        .list();
    experimentIndicatorViewBaseInfoDescRsEntityList.forEach(experimentIndicatorViewBaseInfoDescRsEntity -> {
      String indicatorInstanceIdArray = experimentIndicatorViewBaseInfoDescRsEntity.getIndicatorInstanceIdArray();
      String name = experimentIndicatorViewBaseInfoDescRsEntity.getName();
      List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
      populateExperimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
          kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
      experimentIndicatorViewBaseInfoDescrRsResponseList.add(ExperimentIndicatorViewBaseInfoDescrRsResponse
          .builder()
          .name(name)
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
      final int loopNum=Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size());
      for (int i = 0; i < loopNum; i++) {
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
      if (Objects.nonNull(experimentIndicatorInstanceRsResponse)) {
      ExperimentIndicatorViewBaseInfoSingleRsResponse experimentIndicatorViewBaseInfoSingleRsResponse = ExperimentIndicatorViewBaseInfoSingleRsResponse
          .builder()
          .experimentIndicatorInstanceRsResponse(experimentIndicatorInstanceRsResponse)
          .build();
        experimentIndicatorViewBaseInfoSingleRsResponseList.add(experimentIndicatorViewBaseInfoSingleRsResponse);
      }
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
      if (Objects.nonNull(experimentIndicatorInstanceRsResponse)) {
        experimentIndicatorInstanceRsResponseList.add(experimentIndicatorInstanceRsResponse);
      }
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
      return null;
    }
    return ExperimentIndicatorInstanceRsResponse.getExperimentIndicatorInstanceRsResponse(
        experimentIndicatorInstanceRsEntity.getIndicatorName(), kExperimentIndicatorInstanceIdVValMap.get(experimentIndicatorInstanceId), experimentIndicatorInstanceRsEntity.getUnit()
    );
  }
}
