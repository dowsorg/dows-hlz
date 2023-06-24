package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewSupportExamRsResponse;
import org.dows.hep.api.base.indicator.response.FirstSupportExamTabRsResponse;
import org.dows.hep.api.base.indicator.response.SecondSupportExamTabRsResponse;
import org.dows.hep.api.base.indicator.response.ThirdSupportExamTabRsResponse;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.ExperimentIndicatorViewSupportExamRsEntity;
import org.dows.hep.service.ExperimentIndicatorViewSupportExamRsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewSupportExamRsBiz {
  private final ExperimentIndicatorViewSupportExamRsService experimentIndicatorViewSupportExamRsService;

  public static ExperimentIndicatorViewSupportExamRsResponse experimentIndicatorViewSupportExamRs2Response(ExperimentIndicatorViewSupportExamRsEntity experimentIndicatorViewSupportExamRsEntity) {
    if (Objects.isNull(experimentIndicatorViewSupportExamRsEntity)) {
      return null;
    }
    return ExperimentIndicatorViewSupportExamRsResponse
        .builder()
        .experimentIndicatorViewSupportExamId(experimentIndicatorViewSupportExamRsEntity.getExperimentIndicatorViewSupportExamId())
        .indicatorViewSupportExamId(experimentIndicatorViewSupportExamRsEntity.getIndicatorViewSupportExamId())
        .experimentId(experimentIndicatorViewSupportExamRsEntity.getExperimentId())
        .caseId(experimentIndicatorViewSupportExamRsEntity.getCaseId())
        .appId(experimentIndicatorViewSupportExamRsEntity.getAppId())
        .name(experimentIndicatorViewSupportExamRsEntity.getName())
        .fee(experimentIndicatorViewSupportExamRsEntity.getFee())
        .indicatorInstanceId(experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId())
        .resultAnalysis(experimentIndicatorViewSupportExamRsEntity.getResultAnalysis())
        .status(experimentIndicatorViewSupportExamRsEntity.getStatus())
        .indicatorCategoryIdArray(experimentIndicatorViewSupportExamRsEntity.getIndicatorCategoryIdArray())
        .indicatorCategoryNameArray(experimentIndicatorViewSupportExamRsEntity.getIndicatorCategoryNameArray())
        .build();
  }

  public List<FirstSupportExamTabRsResponse> get(String indicatorFuncId) {
    Map<String, FirstSupportExamTabRsResponse> kIndicatorCategoryIdVFirstSupportExamTabRsResponseMap = new HashMap<>();
    Map<String, SecondSupportExamTabRsResponse> kIndicatorCategoryIdVSecondSupportExamTabRsResponseMap = new HashMap<>();
    Map<String, ThirdSupportExamTabRsResponse> kIndicatorCategoryIdVThirdSupportExamTabRsResponseMap = new HashMap<>();
    experimentIndicatorViewSupportExamRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewSupportExamRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorViewSupportExamRsEntity -> {
          String indicatorCategoryIdArray = experimentIndicatorViewSupportExamRsEntity.getIndicatorCategoryIdArray();
          List<String> indicatorCategoryIdList = Arrays.stream(indicatorCategoryIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorCategoryNameArray = experimentIndicatorViewSupportExamRsEntity.getIndicatorCategoryNameArray();
          String firstIndicatorCategoryId = indicatorCategoryIdList.get(0);
          String secondIndicatorCategoryId = indicatorCategoryIdList.get(1);
          String thirdIndicatorCategoryId = indicatorCategoryIdList.get(2);
          List<String> indicatorCategoryNameList = Arrays.stream(indicatorCategoryNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String firstIndicatorCategoryName = indicatorCategoryNameList.get(0);
          String secondIndicatorCategoryName = indicatorCategoryNameList.get(1);
          String thirdIndicatorCategoryName = indicatorCategoryNameList.get(2);
          ThirdSupportExamTabRsResponse thirdSupportExamTabRsResponse = kIndicatorCategoryIdVThirdSupportExamTabRsResponseMap.get(thirdIndicatorCategoryId);
          if (Objects.isNull(thirdSupportExamTabRsResponse)) {
            thirdSupportExamTabRsResponse = ThirdSupportExamTabRsResponse
                .builder()
                .indicatorCategoryId(thirdIndicatorCategoryId)
                .indicatorCategoryName(thirdIndicatorCategoryName)
                .experimentIndicatorViewSupportExamRsResponseList(new ArrayList<>())
                .build();
          }
          List<ExperimentIndicatorViewSupportExamRsResponse> experimentIndicatorViewSupportExamRsResponseList = thirdSupportExamTabRsResponse.getExperimentIndicatorViewSupportExamRsResponseList();
          if (Objects.isNull(experimentIndicatorViewSupportExamRsResponseList)) {
            experimentIndicatorViewSupportExamRsResponseList = new ArrayList<>();
          }
          experimentIndicatorViewSupportExamRsResponseList.add(experimentIndicatorViewSupportExamRs2Response(experimentIndicatorViewSupportExamRsEntity));
          thirdSupportExamTabRsResponse.setExperimentIndicatorViewSupportExamRsResponseList(experimentIndicatorViewSupportExamRsResponseList);
          kIndicatorCategoryIdVThirdSupportExamTabRsResponseMap.put(thirdIndicatorCategoryId, thirdSupportExamTabRsResponse);
          SecondSupportExamTabRsResponse secondSupportExamTabRsResponse = kIndicatorCategoryIdVSecondSupportExamTabRsResponseMap.get(secondIndicatorCategoryId);
          if (Objects.isNull(secondSupportExamTabRsResponse)) {
            secondSupportExamTabRsResponse = SecondSupportExamTabRsResponse
                .builder()
                .indicatorCategoryId(secondIndicatorCategoryId)
                .indicatorCategoryName(secondIndicatorCategoryName)
                .thirdSupportExamTabRsResponseList(new ArrayList<>())
                .build();
          }
          List<ThirdSupportExamTabRsResponse> thirdSupportExamTabRsResponseList = secondSupportExamTabRsResponse.getThirdSupportExamTabRsResponseList();
          if (Objects.isNull(thirdSupportExamTabRsResponseList)) {
            thirdSupportExamTabRsResponseList = new ArrayList<>();
          }
          thirdSupportExamTabRsResponseList.add(thirdSupportExamTabRsResponse);
          secondSupportExamTabRsResponse.setThirdSupportExamTabRsResponseList(thirdSupportExamTabRsResponseList);
          kIndicatorCategoryIdVSecondSupportExamTabRsResponseMap.put(secondIndicatorCategoryId, secondSupportExamTabRsResponse);
          FirstSupportExamTabRsResponse firstSupportExamTabRsResponse = kIndicatorCategoryIdVFirstSupportExamTabRsResponseMap.get(firstIndicatorCategoryId);
          if (Objects.isNull(firstSupportExamTabRsResponse)) {
            firstSupportExamTabRsResponse = FirstSupportExamTabRsResponse
                .builder()
                .indicatorCategoryId(firstIndicatorCategoryId)
                .indicatorCategoryName(firstIndicatorCategoryName)
                .secondSupportExamTabRsResponseList(new ArrayList<>())
                .build();
          }
          List<SecondSupportExamTabRsResponse> secondSupportExamTabRsResponseList = firstSupportExamTabRsResponse.getSecondSupportExamTabRsResponseList();
          if (Objects.isNull(secondSupportExamTabRsResponseList)) {
            secondSupportExamTabRsResponseList = new ArrayList<>();
          }
          secondSupportExamTabRsResponseList.add(secondSupportExamTabRsResponse);
          firstSupportExamTabRsResponse.setSecondSupportExamTabRsResponseList(secondSupportExamTabRsResponseList);
          kIndicatorCategoryIdVFirstSupportExamTabRsResponseMap.put(firstIndicatorCategoryId, firstSupportExamTabRsResponse);
        });
    kIndicatorCategoryIdVFirstSupportExamTabRsResponseMap.forEach((firstId, firstRs) -> {
      List<SecondSupportExamTabRsResponse> secondSupportExamTabRsResponseList = firstRs.getSecondSupportExamTabRsResponseList();
      secondSupportExamTabRsResponseList.forEach(secondSupportExamTabRsResponse -> {
        List<ThirdSupportExamTabRsResponse> thirdSupportExamTabRsResponseList = secondSupportExamTabRsResponse.getThirdSupportExamTabRsResponseList();
        thirdSupportExamTabRsResponseList.forEach(thirdSupportExamTabRsResponse -> {
          thirdSupportExamTabRsResponse.getExperimentIndicatorViewSupportExamRsResponseList().sort(Comparator.comparing(ExperimentIndicatorViewSupportExamRsResponse::getName));
        });
        thirdSupportExamTabRsResponseList.sort(Comparator.comparing(ThirdSupportExamTabRsResponse::getIndicatorCategoryName));
      });
      secondSupportExamTabRsResponseList.sort(Comparator.comparing(SecondSupportExamTabRsResponse::getIndicatorCategoryName));
    });
    return kIndicatorCategoryIdVFirstSupportExamTabRsResponseMap.values()
        .stream()
        .sorted(Comparator.comparing(FirstSupportExamTabRsResponse::getIndicatorCategoryName))
        .collect(Collectors.toList());
  }
}
