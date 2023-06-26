package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorJudgeDiseaseProblemRsResponse;
import org.dows.hep.api.base.indicator.response.FirstDiseaseProblemTabRsResponse;
import org.dows.hep.api.base.indicator.response.SecondDiseaseProblemTabRsResponse;
import org.dows.hep.api.base.indicator.response.ThirdDiseaseProblemTabRsResponse;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.ExperimentIndicatorJudgeDiseaseProblemRsEntity;
import org.dows.hep.service.ExperimentIndicatorJudgeDiseaseProblemRsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorJudgeDiseaseProblemRsBiz {
  private final ExperimentIndicatorJudgeDiseaseProblemRsService experimentIndicatorJudgeDiseaseProblemRsService;

  public static ExperimentIndicatorJudgeDiseaseProblemRsResponse experimentIndicatorJudgeDiseaseProblemRs2Response(ExperimentIndicatorJudgeDiseaseProblemRsEntity experimentIndicatorJudgeDiseaseProblemRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeDiseaseProblemRsEntity)) {
      return null;
    }
    return ExperimentIndicatorJudgeDiseaseProblemRsResponse
        .builder()
        .experimentIndicatorJudgeDiseaseProblemId(experimentIndicatorJudgeDiseaseProblemRsEntity.getExperimentIndicatorJudgeDiseaseProblemId())
        .indicatorJudgeDiseaseProblemId(experimentIndicatorJudgeDiseaseProblemRsEntity.getIndicatorJudgeDiseaseProblemId())
        .experimentId(experimentIndicatorJudgeDiseaseProblemRsEntity.getExperimentId())
        .caseId(experimentIndicatorJudgeDiseaseProblemRsEntity.getCaseId())
        .appId(experimentIndicatorJudgeDiseaseProblemRsEntity.getAppId())
        .indicatorFuncId(experimentIndicatorJudgeDiseaseProblemRsEntity.getIndicatorFuncId())
        .name(experimentIndicatorJudgeDiseaseProblemRsEntity.getName())
        .point(experimentIndicatorJudgeDiseaseProblemRsEntity.getPoint())
        .resultExplain(experimentIndicatorJudgeDiseaseProblemRsEntity.getResultExplain())
        .status(experimentIndicatorJudgeDiseaseProblemRsEntity.getStatus())
        .indicatorCategoryIdArray(experimentIndicatorJudgeDiseaseProblemRsEntity.getIndicatorCategoryIdArray())
        .indicatorCategoryNameArray(experimentIndicatorJudgeDiseaseProblemRsEntity.getIndicatorCategoryNameArray())
        .build();
  }

  public List<FirstDiseaseProblemTabRsResponse> get(String indicatorFuncId) {
    Map<String, FirstDiseaseProblemTabRsResponse> kIndicatorCategoryIdVFirstDiseaseProblemTabRsResponseMap = new HashMap<>();
    Map<String, SecondDiseaseProblemTabRsResponse> kIndicatorCategoryIdVSecondDiseaseProblemTabRsResponseMap = new HashMap<>();
    Map<String, ThirdDiseaseProblemTabRsResponse> kIndicatorCategoryIdVThirdDiseaseProblemTabRsResponseMap = new HashMap<>();
    experimentIndicatorJudgeDiseaseProblemRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeDiseaseProblemRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorJudgeDiseaseProblemRsEntity -> {
          String indicatorCategoryIdArray = experimentIndicatorJudgeDiseaseProblemRsEntity.getIndicatorCategoryIdArray();
          List<String> indicatorCategoryIdList = Arrays.stream(indicatorCategoryIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorCategoryNameArray = experimentIndicatorJudgeDiseaseProblemRsEntity.getIndicatorCategoryNameArray();
          String firstIndicatorCategoryId = indicatorCategoryIdList.get(0);
          String secondIndicatorCategoryId = indicatorCategoryIdList.get(1);
          String thirdIndicatorCategoryId = indicatorCategoryIdList.get(2);
          List<String> indicatorCategoryNameList = Arrays.stream(indicatorCategoryNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String firstIndicatorCategoryName = indicatorCategoryNameList.get(0);
          String secondIndicatorCategoryName = indicatorCategoryNameList.get(1);
          String thirdIndicatorCategoryName = indicatorCategoryNameList.get(2);
          ThirdDiseaseProblemTabRsResponse thirdDiseaseProblemTabRsResponse = kIndicatorCategoryIdVThirdDiseaseProblemTabRsResponseMap.get(thirdIndicatorCategoryId);
          if (Objects.isNull(thirdDiseaseProblemTabRsResponse)) {
            thirdDiseaseProblemTabRsResponse = ThirdDiseaseProblemTabRsResponse
                .builder()
                .indicatorCategoryId(thirdIndicatorCategoryId)
                .indicatorCategoryName(thirdIndicatorCategoryName)
                .children(new ArrayList<>())
                .build();
          }
          List<ExperimentIndicatorJudgeDiseaseProblemRsResponse> experimentIndicatorJudgeDiseaseProblemRsResponseList = thirdDiseaseProblemTabRsResponse.getChildren();
          if (Objects.isNull(experimentIndicatorJudgeDiseaseProblemRsResponseList)) {
            experimentIndicatorJudgeDiseaseProblemRsResponseList = new ArrayList<>();
          }
          experimentIndicatorJudgeDiseaseProblemRsResponseList.add(experimentIndicatorJudgeDiseaseProblemRs2Response(experimentIndicatorJudgeDiseaseProblemRsEntity));
          thirdDiseaseProblemTabRsResponse.setChildren(experimentIndicatorJudgeDiseaseProblemRsResponseList);
          kIndicatorCategoryIdVThirdDiseaseProblemTabRsResponseMap.put(thirdIndicatorCategoryId, thirdDiseaseProblemTabRsResponse);
          SecondDiseaseProblemTabRsResponse secondDiseaseProblemTabRsResponse = kIndicatorCategoryIdVSecondDiseaseProblemTabRsResponseMap.get(secondIndicatorCategoryId);
          if (Objects.isNull(secondDiseaseProblemTabRsResponse)) {
            secondDiseaseProblemTabRsResponse = SecondDiseaseProblemTabRsResponse
                .builder()
                .indicatorCategoryId(secondIndicatorCategoryId)
                .indicatorCategoryName(secondIndicatorCategoryName)
                .children(new ArrayList<>())
                .build();
          }
          List<ThirdDiseaseProblemTabRsResponse> thirdDiseaseProblemTabRsResponseList = secondDiseaseProblemTabRsResponse.getChildren();
          if (Objects.isNull(thirdDiseaseProblemTabRsResponseList)) {
            thirdDiseaseProblemTabRsResponseList = new ArrayList<>();
          }
          thirdDiseaseProblemTabRsResponseList.add(thirdDiseaseProblemTabRsResponse);
          secondDiseaseProblemTabRsResponse.setChildren(thirdDiseaseProblemTabRsResponseList);
          kIndicatorCategoryIdVSecondDiseaseProblemTabRsResponseMap.put(secondIndicatorCategoryId, secondDiseaseProblemTabRsResponse);
          FirstDiseaseProblemTabRsResponse firstDiseaseProblemTabRsResponse = kIndicatorCategoryIdVFirstDiseaseProblemTabRsResponseMap.get(firstIndicatorCategoryId);
          if (Objects.isNull(firstDiseaseProblemTabRsResponse)) {
            firstDiseaseProblemTabRsResponse = FirstDiseaseProblemTabRsResponse
                .builder()
                .indicatorCategoryId(firstIndicatorCategoryId)
                .indicatorCategoryName(firstIndicatorCategoryName)
                .children(new ArrayList<>())
                .build();
          }
          List<SecondDiseaseProblemTabRsResponse> secondDiseaseProblemTabRsResponseList = firstDiseaseProblemTabRsResponse.getChildren();
          if (Objects.isNull(secondDiseaseProblemTabRsResponseList)) {
            secondDiseaseProblemTabRsResponseList = new ArrayList<>();
          }
          secondDiseaseProblemTabRsResponseList.add(secondDiseaseProblemTabRsResponse);
          firstDiseaseProblemTabRsResponse.setChildren(secondDiseaseProblemTabRsResponseList);
          kIndicatorCategoryIdVFirstDiseaseProblemTabRsResponseMap.put(firstIndicatorCategoryId, firstDiseaseProblemTabRsResponse);
        });
    kIndicatorCategoryIdVFirstDiseaseProblemTabRsResponseMap.forEach((firstId, firstRs) -> {
      List<SecondDiseaseProblemTabRsResponse> secondDiseaseProblemTabRsResponseList = firstRs.getChildren();
      secondDiseaseProblemTabRsResponseList.forEach(secondDiseaseProblemTabRsResponse -> {
        List<ThirdDiseaseProblemTabRsResponse> thirdDiseaseProblemTabRsResponseList = secondDiseaseProblemTabRsResponse.getChildren();
        thirdDiseaseProblemTabRsResponseList.forEach(thirdDiseaseProblemTabRsResponse -> {
          thirdDiseaseProblemTabRsResponse.getChildren().sort(Comparator.comparing(ExperimentIndicatorJudgeDiseaseProblemRsResponse::getName));
        });
        thirdDiseaseProblemTabRsResponseList.sort(Comparator.comparing(ThirdDiseaseProblemTabRsResponse::getIndicatorCategoryName));
      });
      secondDiseaseProblemTabRsResponseList.sort(Comparator.comparing(SecondDiseaseProblemTabRsResponse::getIndicatorCategoryName));
    });
    return kIndicatorCategoryIdVFirstDiseaseProblemTabRsResponseMap.values()
        .stream()
        .sorted(Comparator.comparing(FirstDiseaseProblemTabRsResponse::getIndicatorCategoryName))
        .collect(Collectors.toList());
  }
}
