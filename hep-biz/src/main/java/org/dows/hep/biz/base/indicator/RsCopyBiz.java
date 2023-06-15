package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CopyViewIndicatorRequestRs;
import org.dows.hep.api.base.indicator.request.JudgeViewIndicatorRequestRs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCopyBiz {
  @Transactional(rollbackFor = Exception.class)
  public void rsCopyViewIndicator(CopyViewIndicatorRequestRs copyViewIndicatorRequestRs) {

  }

  @Transactional(rollbackFor = Exception.class)
  public void rsCopyJudgeIndicator(JudgeViewIndicatorRequestRs judgeViewIndicatorRequestRs) {

  }
}
