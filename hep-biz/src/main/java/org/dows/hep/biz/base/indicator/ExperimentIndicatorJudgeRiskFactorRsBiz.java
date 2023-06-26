package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorJudgeRiskFactorRsResponse;
import org.dows.hep.api.base.indicator.response.FirstRiskFactorTabRsResponse;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorRsEntity;
import org.dows.hep.service.ExperimentIndicatorJudgeRiskFactorRsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorJudgeRiskFactorRsBiz {
  private final ExperimentIndicatorJudgeRiskFactorRsService experimentIndicatorJudgeRiskFactorRsService;

  public static ExperimentIndicatorJudgeRiskFactorRsResponse experimentIndicatorJudgeRiskFactorRs2Response(ExperimentIndicatorJudgeRiskFactorRsEntity experimentIndicatorJudgeRiskFactorRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeRiskFactorRsEntity)) {
      return null;
    }
    return ExperimentIndicatorJudgeRiskFactorRsResponse
        .builder()
        .experimentIndicatorJudgeRiskFactorId(experimentIndicatorJudgeRiskFactorRsEntity.getExperimentIndicatorJudgeRiskFactorId())
        .indicatorJudgeRiskFactorId(experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorJudgeRiskFactorId())
        .experimentId(experimentIndicatorJudgeRiskFactorRsEntity.getExperimentId())
        .caseId(experimentIndicatorJudgeRiskFactorRsEntity.getCaseId())
        .appId(experimentIndicatorJudgeRiskFactorRsEntity.getAppId())
        .indicatorFuncId(experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorFuncId())
        .name(experimentIndicatorJudgeRiskFactorRsEntity.getName())
        .point(experimentIndicatorJudgeRiskFactorRsEntity.getPoint())
        .resultExplain(experimentIndicatorJudgeRiskFactorRsEntity.getResultExplain())
        .status(experimentIndicatorJudgeRiskFactorRsEntity.getStatus())
        .indicatorCategoryIdArray(experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorCategoryIdArray())
        .indicatorCategoryNameArray(experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorCategoryNameArray())
        .build();
  }

  public List<FirstRiskFactorTabRsResponse> get(String indicatorFuncId) {
    Map<String, FirstRiskFactorTabRsResponse> kIndicatorCategoryIdVFirstRiskFactorTabRsResponseMap = new HashMap<>();
    experimentIndicatorJudgeRiskFactorRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeRiskFactorRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorJudgeRiskFactorRsEntity -> {
          String indicatorCategoryIdArray = experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorCategoryIdArray();
          List<String> indicatorCategoryIdList = Arrays.stream(indicatorCategoryIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorCategoryNameArray = experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorCategoryNameArray();
          String firstIndicatorCategoryId = indicatorCategoryIdList.get(0);
          List<String> indicatorCategoryNameList = Arrays.stream(indicatorCategoryNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String firstIndicatorCategoryName = indicatorCategoryNameList.get(0);
          FirstRiskFactorTabRsResponse firstRiskFactorTabRsResponse = kIndicatorCategoryIdVFirstRiskFactorTabRsResponseMap.get(firstIndicatorCategoryId);
          if (Objects.isNull(firstRiskFactorTabRsResponse)) {
            firstRiskFactorTabRsResponse = FirstRiskFactorTabRsResponse
                .builder()
                .indicatorCategoryId(firstIndicatorCategoryId)
                .indicatorCategoryName(firstIndicatorCategoryName)
                .experimentIndicatorJudgeRiskFactorRsResponseList(new ArrayList<>())
                .build();
          }
          List<ExperimentIndicatorJudgeRiskFactorRsResponse> experimentIndicatorJudgeRiskFactorRsResponseList = firstRiskFactorTabRsResponse.getExperimentIndicatorJudgeRiskFactorRsResponseList();
          if (Objects.isNull(experimentIndicatorJudgeRiskFactorRsResponseList)) {
            experimentIndicatorJudgeRiskFactorRsResponseList = new ArrayList<>();
          }
          experimentIndicatorJudgeRiskFactorRsResponseList.add(experimentIndicatorJudgeRiskFactorRs2Response(experimentIndicatorJudgeRiskFactorRsEntity));
          firstRiskFactorTabRsResponse.setExperimentIndicatorJudgeRiskFactorRsResponseList(experimentIndicatorJudgeRiskFactorRsResponseList);
          kIndicatorCategoryIdVFirstRiskFactorTabRsResponseMap.put(firstIndicatorCategoryId, firstRiskFactorTabRsResponse);
        });
    kIndicatorCategoryIdVFirstRiskFactorTabRsResponseMap.forEach((firstId, firstRs) -> {
      List<ExperimentIndicatorJudgeRiskFactorRsResponse> experimentIndicatorJudgeRiskFactorRsResponseList = firstRs.getExperimentIndicatorJudgeRiskFactorRsResponseList();
      experimentIndicatorJudgeRiskFactorRsResponseList.sort(Comparator.comparing(ExperimentIndicatorJudgeRiskFactorRsResponse::getName));
    });
    return kIndicatorCategoryIdVFirstRiskFactorTabRsResponseMap.values()
        .stream()
        .sorted(Comparator.comparing(FirstRiskFactorTabRsResponse::getIndicatorCategoryName))
        .collect(Collectors.toList());
  }
}
