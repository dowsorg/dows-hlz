package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
public class CaseOrgModuleFuncRefBiz {

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseOrgModuleFuncRefId) {
  }
}
