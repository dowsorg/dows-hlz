package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.CaseOrgModuleFuncRefResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.CaseOrgModuleFuncRefException;
import org.dows.hep.entity.CaseOrgModuleFuncRefEntity;
import org.dows.hep.service.CaseOrgModuleFuncRefService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseOrgModuleFuncRefBiz {
  private final CaseOrgModuleFuncRefService caseOrgModuleFuncRefService;

  public CaseOrgModuleFuncRefResponseRs caseOrgModuleFuncRef2ResponseRs(
      CaseOrgModuleFuncRefEntity caseOrgModuleFuncRefEntity,
      IndicatorFuncResponse indicatorFuncResponse
  ) {
    if (Objects.isNull(caseOrgModuleFuncRefEntity)) {
      return null;
    }
    return CaseOrgModuleFuncRefResponseRs
        .builder()
        .caseOrgModuleFuncRefId(caseOrgModuleFuncRefEntity.getCaseOrgModuleFuncRefId())
        .appId(caseOrgModuleFuncRefEntity.getAppId())
        .caseOrgModuleId(caseOrgModuleFuncRefEntity.getCaseOrgModuleId())
        .indicatorFuncResponse(indicatorFuncResponse)
        .deleted(caseOrgModuleFuncRefEntity.getDeleted())
        .dt(caseOrgModuleFuncRefEntity.getDt())
        .build();
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseOrgModuleFuncRefId) {
    boolean isRemove = caseOrgModuleFuncRefService.remove(
        new LambdaQueryWrapper<CaseOrgModuleFuncRefEntity>()
            .eq(CaseOrgModuleFuncRefEntity::getCaseOrgModuleFuncRefId, caseOrgModuleFuncRefId)
    );
    if (!isRemove) {
      log.warn("method CaseOrgModuleFuncRefBiz.delete caseOrgModuleFuncRefId:{} is illegal", caseOrgModuleFuncRefId);
      throw new CaseOrgModuleFuncRefException(EnumESC.VALIDATE_EXCEPTION);
    }
  }
}
