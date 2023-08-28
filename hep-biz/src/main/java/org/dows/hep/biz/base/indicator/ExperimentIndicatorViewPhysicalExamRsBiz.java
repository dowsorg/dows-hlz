package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewPhysicalExamRsResponse;
import org.dows.hep.api.base.indicator.response.FirstPhysicalExamTabRsResponse;
import org.dows.hep.entity.ExperimentIndicatorViewPhysicalExamRsEntity;
import org.dows.hep.service.ExperimentIndicatorViewPhysicalExamRsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewPhysicalExamRsBiz {
  private final ExperimentIndicatorViewPhysicalExamRsService experimentIndicatorViewPhysicalExamRsService;

  public static ExperimentIndicatorViewPhysicalExamRsResponse experimentIndicatorViewPhysicalExamRs2Response(ExperimentIndicatorViewPhysicalExamRsEntity experimentIndicatorViewPhysicalExamRsEntity) {
    if (Objects.isNull(experimentIndicatorViewPhysicalExamRsEntity)) {
      return null;
    }
    return ExperimentIndicatorViewPhysicalExamRsResponse
        .builder()
        .experimentIndicatorViewPhysicalExamId(experimentIndicatorViewPhysicalExamRsEntity.getExperimentIndicatorViewPhysicalExamId())
        .indicatorViewPhysicalExamId(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorViewPhysicalExamId())
        .experimentId(experimentIndicatorViewPhysicalExamRsEntity.getExperimentId())
        .caseId(experimentIndicatorViewPhysicalExamRsEntity.getCaseId())
        .appId(experimentIndicatorViewPhysicalExamRsEntity.getAppId())
        .indicatorFuncId(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorFuncId())
        .name(experimentIndicatorViewPhysicalExamRsEntity.getName())
        .fee(experimentIndicatorViewPhysicalExamRsEntity.getFee())
        .indicatorInstanceId(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId())
        .resultAnalysis(experimentIndicatorViewPhysicalExamRsEntity.getResultAnalysis())
        .status(experimentIndicatorViewPhysicalExamRsEntity.getStatus())
        .indicatorCategoryId(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorCategoryId())
        .indicatorCategoryName(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorCategoryName())
        .indicatorCategoryDt(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorCategoryDt())
        .build();
  }

  public List<FirstPhysicalExamTabRsResponse> get(String indicatorFuncId) {
    Map<String, FirstPhysicalExamTabRsResponse> kIndicatorCategoryIdVFirstPhysicalExamTabRsResponseMap = new HashMap<>();
    experimentIndicatorViewPhysicalExamRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewPhysicalExamRsEntity::getIndicatorFuncId, indicatorFuncId)
        .list()
        .forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
          String indicatorCategoryId = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorCategoryId();
          String indicatorCategoryName = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorCategoryName();
          Date indicatorCategoryDt = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorCategoryDt();
          FirstPhysicalExamTabRsResponse firstPhysicalExamTabRsResponse = kIndicatorCategoryIdVFirstPhysicalExamTabRsResponseMap.get(indicatorCategoryId);
          if (Objects.isNull(firstPhysicalExamTabRsResponse)) {
            firstPhysicalExamTabRsResponse = FirstPhysicalExamTabRsResponse
                .builder()
                .id(experimentIndicatorViewPhysicalExamRsEntity.getId())
                .indicatorCategoryId(indicatorCategoryId)
                .indicatorCategoryName(indicatorCategoryName)
                .indicatorCategoryDt(indicatorCategoryDt)
                .children(new ArrayList<>())
                .build();
          }
          List<ExperimentIndicatorViewPhysicalExamRsResponse> experimentIndicatorViewPhysicalExamRsResponseList = firstPhysicalExamTabRsResponse.getChildren();
          if (Objects.isNull(experimentIndicatorViewPhysicalExamRsResponseList)) {
            experimentIndicatorViewPhysicalExamRsResponseList = new ArrayList<>();
          }
          experimentIndicatorViewPhysicalExamRsResponseList.add(experimentIndicatorViewPhysicalExamRs2Response(experimentIndicatorViewPhysicalExamRsEntity));
          firstPhysicalExamTabRsResponse.setChildren(experimentIndicatorViewPhysicalExamRsResponseList);
          kIndicatorCategoryIdVFirstPhysicalExamTabRsResponseMap.put(indicatorCategoryId, firstPhysicalExamTabRsResponse);
        });
    return kIndicatorCategoryIdVFirstPhysicalExamTabRsResponseMap.values().stream()
        .sorted(Comparator.comparingLong(firstPhysicalExamTabRsResponse -> firstPhysicalExamTabRsResponse.getId()))
        .collect(Collectors.toList());
  }
}
