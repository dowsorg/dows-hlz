package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorJudgeHealthProblemRsResponse;
import org.dows.hep.api.base.indicator.response.FirstHealthProblemTabRsResponse;
import org.dows.hep.api.base.indicator.response.SecondHealthProblemTabRsResponse;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemRsEntity;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthProblemRsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorJudgeHealthProblemRsBiz {
  private final ExperimentIndicatorJudgeHealthProblemRsService experimentIndicatorJudgeHealthProblemRsService;

  public static ExperimentIndicatorJudgeHealthProblemRsResponse experimentIndicatorJudgeHealthProblemRs2Response(ExperimentIndicatorJudgeHealthProblemRsEntity experimentIndicatorJudgeHealthProblemRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeHealthProblemRsEntity)) {
      return null;
    }
    return ExperimentIndicatorJudgeHealthProblemRsResponse
        .builder()
        .experimentIndicatorJudgeHealthProblemId(experimentIndicatorJudgeHealthProblemRsEntity.getExperimentIndicatorJudgeHealthProblemId())
        .indicatorJudgeHealthProblemId(experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorJudgeHealthProblemId())
        .experimentId(experimentIndicatorJudgeHealthProblemRsEntity.getExperimentId())
        .caseId(experimentIndicatorJudgeHealthProblemRsEntity.getCaseId())
        .appId(experimentIndicatorJudgeHealthProblemRsEntity.getAppId())
        .indicatorFuncId(experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorFuncId())
        .name(experimentIndicatorJudgeHealthProblemRsEntity.getName())
        .point(experimentIndicatorJudgeHealthProblemRsEntity.getPoint())
        .resultExplain(experimentIndicatorJudgeHealthProblemRsEntity.getResultExplain())
        .status(experimentIndicatorJudgeHealthProblemRsEntity.getStatus())
        .indicatorCategoryIdArray(experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorCategoryIdArray())
        .indicatorCategoryNameArray(experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorCategoryNameArray())
        .build();
  }

  public List<FirstHealthProblemTabRsResponse> get(String indicatorFuncId) {
    Map<String, FirstHealthProblemTabRsResponse> kIndicatorCategoryIdVFirstHealthProblemTabRsResponseMap = new HashMap<>();
    Map<String, SecondHealthProblemTabRsResponse> kIndicatorCategoryIdVSecondHealthProblemTabRsResponseMap = new HashMap<>();
    experimentIndicatorJudgeHealthProblemRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthProblemRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorJudgeHealthProblemRsEntity -> {
          String indicatorCategoryIdArray = experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorCategoryIdArray();
          List<String> indicatorCategoryIdList = Arrays.stream(indicatorCategoryIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorCategoryNameArray = experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorCategoryNameArray();
          String firstIndicatorCategoryId = indicatorCategoryIdList.get(0);
          String secondIndicatorCategoryId = indicatorCategoryIdList.get(1);
          List<String> indicatorCategoryNameList = Arrays.stream(indicatorCategoryNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String firstIndicatorCategoryName = indicatorCategoryNameList.get(0);
          String secondIndicatorCategoryName = indicatorCategoryNameList.get(1);
          SecondHealthProblemTabRsResponse secondHealthProblemTabRsResponse = kIndicatorCategoryIdVSecondHealthProblemTabRsResponseMap.get(secondIndicatorCategoryId);
          if (Objects.isNull(secondHealthProblemTabRsResponse)) {
            secondHealthProblemTabRsResponse = SecondHealthProblemTabRsResponse
                .builder()
                .indicatorCategoryId(secondIndicatorCategoryId)
                .indicatorCategoryName(secondIndicatorCategoryName)
                .children(new ArrayList<>())
                .build();
          }
          List<ExperimentIndicatorJudgeHealthProblemRsResponse> experimentIndicatorJudgeHealthProblemRsResponseList = secondHealthProblemTabRsResponse.getChildren();
          if (Objects.isNull(experimentIndicatorJudgeHealthProblemRsResponseList)) {
            experimentIndicatorJudgeHealthProblemRsResponseList = new ArrayList<>();
          }
          experimentIndicatorJudgeHealthProblemRsResponseList.add(experimentIndicatorJudgeHealthProblemRs2Response(experimentIndicatorJudgeHealthProblemRsEntity));
          secondHealthProblemTabRsResponse.setChildren(experimentIndicatorJudgeHealthProblemRsResponseList);
          kIndicatorCategoryIdVSecondHealthProblemTabRsResponseMap.put(secondIndicatorCategoryId, secondHealthProblemTabRsResponse);
          FirstHealthProblemTabRsResponse firstHealthProblemTabRsResponse = kIndicatorCategoryIdVFirstHealthProblemTabRsResponseMap.get(firstIndicatorCategoryId);
          if (Objects.isNull(firstHealthProblemTabRsResponse)) {
            firstHealthProblemTabRsResponse = FirstHealthProblemTabRsResponse
                .builder()
                .indicatorCategoryId(firstIndicatorCategoryId)
                .indicatorCategoryName(firstIndicatorCategoryName)
                .children(new ArrayList<>())
                .build();
          }
          List<SecondHealthProblemTabRsResponse> secondHealthProblemTabRsResponseList = firstHealthProblemTabRsResponse.getChildren();
          if (Objects.isNull(secondHealthProblemTabRsResponseList)) {
            secondHealthProblemTabRsResponseList = new ArrayList<>();
          }
          if (secondHealthProblemTabRsResponseList.stream().map(SecondHealthProblemTabRsResponse::getIndicatorCategoryId).noneMatch(a -> a.equals(secondIndicatorCategoryId))) {
            secondHealthProblemTabRsResponseList.add(secondHealthProblemTabRsResponse);
          }
          firstHealthProblemTabRsResponse.setChildren(secondHealthProblemTabRsResponseList);
          kIndicatorCategoryIdVFirstHealthProblemTabRsResponseMap.put(firstIndicatorCategoryId, firstHealthProblemTabRsResponse);
        });
    kIndicatorCategoryIdVFirstHealthProblemTabRsResponseMap.forEach((firstId, firstRs) -> {
      List<SecondHealthProblemTabRsResponse> secondHealthProblemTabRsResponseList = firstRs.getChildren();
      secondHealthProblemTabRsResponseList.forEach(secondHealthProblemTabRsResponse -> {
        List<ExperimentIndicatorJudgeHealthProblemRsResponse> experimentIndicatorJudgeHealthProblemRsResponseList = secondHealthProblemTabRsResponse.getChildren();
        experimentIndicatorJudgeHealthProblemRsResponseList.sort(Comparator.comparing(ExperimentIndicatorJudgeHealthProblemRsResponse::getName));
      });
      secondHealthProblemTabRsResponseList.sort(Comparator.comparing(SecondHealthProblemTabRsResponse::getIndicatorCategoryName));
    });
    return kIndicatorCategoryIdVFirstHealthProblemTabRsResponseMap.values()
        .stream()
        .sorted(Comparator.comparing(FirstHealthProblemTabRsResponse::getIndicatorCategoryName))
        .collect(Collectors.toList());
  }
}
