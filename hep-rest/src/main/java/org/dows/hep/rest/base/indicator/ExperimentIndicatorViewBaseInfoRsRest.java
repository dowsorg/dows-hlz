package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.GetIndicatorBaseInfo;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewBaseInfoRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewBaseInfoRsBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标基本信息模块实例", description = "实验查看指标基本信息模块实例")
public class ExperimentIndicatorViewBaseInfoRsRest {
  private final ExperimentIndicatorViewBaseInfoRsBiz experimentIndicatorViewBaseInfoRsBiz;

  @Operation(summary = "根据功能点id和人物获取基本信息")
  @GetMapping("v1/userExperiment/baseInfo/get")
  public ExperimentIndicatorViewBaseInfoRsResponse get(
      @RequestParam String experimentIndicatorViewBaseInfoId,
      @RequestParam String experimentPersonId,
      @RequestParam Integer periods
  ) throws ExecutionException, InterruptedException {
    return experimentIndicatorViewBaseInfoRsBiz.get(null,experimentIndicatorViewBaseInfoId, experimentPersonId, periods);
  }


  @Operation(summary = "获取人物基本信息")
  @GetMapping("v1/userExperiment/getBaseInfo")
  public ExperimentIndicatorViewBaseInfoRsResponse baseInfo(GetIndicatorBaseInfo getIndicatorBaseInfo) throws ExecutionException, InterruptedException {
    return experimentIndicatorViewBaseInfoRsBiz.get(getIndicatorBaseInfo.getExperimentId(),null, getIndicatorBaseInfo.getExperimentPersonId(), getIndicatorBaseInfo.getPeriods());
  }
}
