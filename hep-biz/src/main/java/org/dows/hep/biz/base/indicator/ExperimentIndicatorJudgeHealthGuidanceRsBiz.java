package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorJudgeHealthGuidanceRsResponse;
import org.dows.hep.api.base.indicator.response.FirstHealthGuidanceTabRsResponse;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceRsEntity;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthGuidanceRsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorJudgeHealthGuidanceRsBiz {
  private final ExperimentIndicatorJudgeHealthGuidanceRsService experimentIndicatorJudgeHealthGuidanceRsService;

  public static ExperimentIndicatorJudgeHealthGuidanceRsResponse experimentIndicatorJudgeHealthGuidanceRs2Response(ExperimentIndicatorJudgeHealthGuidanceRsEntity experimentIndicatorJudgeHealthGuidanceRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeHealthGuidanceRsEntity)) {
      return null;
    }
    return ExperimentIndicatorJudgeHealthGuidanceRsResponse
        .builder()
        .experimentIndicatorJudgeHealthGuidanceId(experimentIndicatorJudgeHealthGuidanceRsEntity.getExperimentIndicatorJudgeHealthGuidanceId())
        .indicatorJudgeHealthGuidanceId(experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorJudgeHealthGuidanceId())
        .experimentId(experimentIndicatorJudgeHealthGuidanceRsEntity.getExperimentId())
        .caseId(experimentIndicatorJudgeHealthGuidanceRsEntity.getCaseId())
        .appId(experimentIndicatorJudgeHealthGuidanceRsEntity.getAppId())
        .indicatorFuncId(experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorFuncId())
        .name(experimentIndicatorJudgeHealthGuidanceRsEntity.getName())
        .point(experimentIndicatorJudgeHealthGuidanceRsEntity.getPoint())
        .resultExplain(experimentIndicatorJudgeHealthGuidanceRsEntity.getResultExplain())
        .status(experimentIndicatorJudgeHealthGuidanceRsEntity.getStatus())
        .indicatorCategoryIdArray(experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorCategoryIdArray())
        .indicatorCategoryNameArray(experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorCategoryNameArray())
        .build();
  }

  public List<FirstHealthGuidanceTabRsResponse> get(String indicatorFuncId) {
    Map<String, FirstHealthGuidanceTabRsResponse> kIndicatorCategoryIdVFirstHealthGuidanceTabRsResponseMap = new HashMap<>();
    experimentIndicatorJudgeHealthGuidanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthGuidanceRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorJudgeHealthGuidanceRsEntity -> {
          String indicatorCategoryIdArray = experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorCategoryIdArray();
          List<String> indicatorCategoryIdList = Arrays.stream(indicatorCategoryIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorCategoryNameArray = experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorCategoryNameArray();
          String firstIndicatorCategoryId = indicatorCategoryIdList.get(0);
          List<String> indicatorCategoryNameList = Arrays.stream(indicatorCategoryNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String firstIndicatorCategoryName = indicatorCategoryNameList.get(0);
          FirstHealthGuidanceTabRsResponse firstHealthGuidanceTabRsResponse = kIndicatorCategoryIdVFirstHealthGuidanceTabRsResponseMap.get(firstIndicatorCategoryId);
          if (Objects.isNull(firstHealthGuidanceTabRsResponse)) {
            firstHealthGuidanceTabRsResponse = FirstHealthGuidanceTabRsResponse
                .builder()
                .indicatorCategoryId(firstIndicatorCategoryId)
                .indicatorCategoryName(firstIndicatorCategoryName)
                .experimentIndicatorJudgeHealthGuidanceRsResponseList(new ArrayList<>())
                .build();
          }
          List<ExperimentIndicatorJudgeHealthGuidanceRsResponse> experimentIndicatorJudgeHealthGuidanceRsResponseList = firstHealthGuidanceTabRsResponse.getExperimentIndicatorJudgeHealthGuidanceRsResponseList();
          if (Objects.isNull(experimentIndicatorJudgeHealthGuidanceRsResponseList)) {
            experimentIndicatorJudgeHealthGuidanceRsResponseList = new ArrayList<>();
          }
          experimentIndicatorJudgeHealthGuidanceRsResponseList.add(experimentIndicatorJudgeHealthGuidanceRs2Response(experimentIndicatorJudgeHealthGuidanceRsEntity));
          firstHealthGuidanceTabRsResponse.setExperimentIndicatorJudgeHealthGuidanceRsResponseList(experimentIndicatorJudgeHealthGuidanceRsResponseList);
          kIndicatorCategoryIdVFirstHealthGuidanceTabRsResponseMap.put(firstIndicatorCategoryId, firstHealthGuidanceTabRsResponse);
        });
    kIndicatorCategoryIdVFirstHealthGuidanceTabRsResponseMap.forEach((firstId, firstRs) -> {
      List<ExperimentIndicatorJudgeHealthGuidanceRsResponse> experimentIndicatorJudgeHealthGuidanceRsResponseList = firstRs.getExperimentIndicatorJudgeHealthGuidanceRsResponseList();
      experimentIndicatorJudgeHealthGuidanceRsResponseList.sort(Comparator.comparing(ExperimentIndicatorJudgeHealthGuidanceRsResponse::getName));
    });
    return kIndicatorCategoryIdVFirstHealthGuidanceTabRsResponseMap.values()
        .stream()
        .sorted(Comparator.comparing(FirstHealthGuidanceTabRsResponse::getIndicatorCategoryName))
        .collect(Collectors.toList());
  }
}
