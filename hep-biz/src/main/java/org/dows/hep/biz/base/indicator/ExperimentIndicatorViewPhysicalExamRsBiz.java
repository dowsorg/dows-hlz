package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewPhysicalExamRsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewPhysicalExamRsBiz {
  public List<ExperimentIndicatorViewPhysicalExamRsResponse> get(String experimentIndicatorViewPhysicalExamId, String experimentPersonId) {
    /* runsix:TODO  */
    return new ArrayList<>();
  }
}
