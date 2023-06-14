package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateCaseOrgModuleRequestRs;
import org.dows.hep.api.base.indicator.response.CaseOrgModuleResponseRs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
public class CaseOrgModuleBiz {
  @Transactional(rollbackFor = Exception.class)
  public void batchCreateOrUpdate(BatchCreateOrUpdateCaseOrgModuleRequestRs batchCreateOrUpdateCaseOrgModuleRequestRs) {
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseOrgModuleId) {
  }


  public List<CaseOrgModuleResponseRs> getByCaseOrgId(String appId, String caseOrgId) {
    return null;
  }
}
