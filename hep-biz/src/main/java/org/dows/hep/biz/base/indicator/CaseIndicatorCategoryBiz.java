package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.CaseIndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.CaseIndicatorInstanceCategoryResponseRs;
import org.dows.hep.api.base.indicator.response.CaseIndicatorInstanceResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.entity.CaseIndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
public class CaseIndicatorCategoryBiz {

  public static CaseIndicatorCategoryResponse caseIndicatorCategoryEntity2Response(CaseIndicatorCategoryEntity caseIndicatorCategoryEntity) {
    if (Objects.isNull(caseIndicatorCategoryEntity)) {
      return null;
    }
    return CaseIndicatorCategoryResponse
        .builder()
        .id(caseIndicatorCategoryEntity.getId())
        .caseIndicatorCategoryId(caseIndicatorCategoryEntity.getCaseIndicatorCategoryId())
        .indicatorCategoryId(caseIndicatorCategoryEntity.getIndicatorCategoryId())
        .appId(caseIndicatorCategoryEntity.getAppId())
        .pid(caseIndicatorCategoryEntity.getPid())
        .categoryName(caseIndicatorCategoryEntity.getCategoryName())
        .seq(caseIndicatorCategoryEntity.getSeq())
        .build();
  }
  public static CaseIndicatorInstanceCategoryResponseRs caseIndicatorCategory2ResponseRs(
      CaseIndicatorCategoryEntity caseIndicatorCategoryEntity,
      List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceResponseRsList
  ) {
    if (Objects.isNull(caseIndicatorCategoryEntity)) {
      return null;
    }
    return CaseIndicatorInstanceCategoryResponseRs
        .builder()
        .indicatorCategoryId(caseIndicatorCategoryEntity.getCaseIndicatorCategoryId())
        .appId(caseIndicatorCategoryEntity.getAppId())
        .pid(caseIndicatorCategoryEntity.getPid())
        .categoryName(caseIndicatorCategoryEntity.getCategoryName())
        .seq(caseIndicatorCategoryEntity.getSeq())
        .dt(caseIndicatorCategoryEntity.getDt())
        .caseIndicatorInstanceResponseRsList(caseIndicatorInstanceResponseRsList)
        .build();
  }
}
