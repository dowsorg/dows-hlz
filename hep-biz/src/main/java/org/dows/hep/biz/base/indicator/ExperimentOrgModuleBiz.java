package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorFuncRsResponse;
import org.dows.hep.api.base.indicator.response.ExperimentOrgModuleRsResponse;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.ExperimentOrgModuleRsEntity;
import org.dows.hep.service.ExperimentOrgModuleRsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentOrgModuleBiz {
  private final ExperimentOrgModuleRsService experimentOrgModuleRsService;
  public List<ExperimentOrgModuleRsResponse> getByExperimentOrgIdAndExperimentPersonId(String experimentOrgId) {
    return experimentOrgModuleRsService.lambdaQuery()
        .eq(ExperimentOrgModuleRsEntity::getOrgId, experimentOrgId)
        .orderByAsc(ExperimentOrgModuleRsEntity::getSeq)
        .list()
        .stream()
        .map(experimentOrgModuleRsEntity -> {
          String indicatorFuncIdArray = experimentOrgModuleRsEntity.getIndicatorFuncIdArray();
          List<String> indicatorFuncIdList = Arrays.stream(indicatorFuncIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorFuncNameArray = experimentOrgModuleRsEntity.getIndicatorFuncNameArray();
          List<String> indicatorFuncNameList = Arrays.stream(indicatorFuncNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          String indicatorCategoryIdArray = experimentOrgModuleRsEntity.getIndicatorCategoryIdArray();
          List<String> indicatorCategoryIdList = Arrays.stream(indicatorCategoryIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
          List<ExperimentIndicatorFuncRsResponse> experimentIndicatorFuncRsResponseList = new ArrayList<>();
          if (!indicatorFuncIdList.isEmpty()) {
            for (int i = 0; i <= indicatorFuncIdList.size()-1; i++) {
              experimentIndicatorFuncRsResponseList.add(
                  ExperimentIndicatorFuncRsResponse
                      .builder()
                      .indicatorFuncId(indicatorFuncIdList.get(i))
                      .indicatorFuncName(indicatorFuncNameList.get(i))
                      .indicatorCategoryId(indicatorCategoryIdList.get(i))
                      .seq(i)
                      .build()
              );
            }
          }
          return ExperimentOrgModuleRsResponse
              .builder()
              .experimentOrgModuleId(experimentOrgModuleRsEntity.getExperimentOrgModuleId())
              .caseOrgModuleId(experimentOrgModuleRsEntity.getCaseOrgModuleId())
              .appId(experimentOrgModuleRsEntity.getAppId())
              .orgId(experimentOrgModuleRsEntity.getOrgId())
              .name(experimentOrgModuleRsEntity.getName())
              .experimentIndicatorFuncRsResponseList(experimentIndicatorFuncRsResponseList)
              .seq(experimentOrgModuleRsEntity.getSeq())
              .build();
        }).collect(Collectors.toList());
  }
}
