package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateCaseOrgModuleRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateCaseOrgModuleRequestRs;
import org.dows.hep.api.base.indicator.response.CaseOrgModuleResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.CaseOrgModuleException;
import org.dows.hep.entity.CaseOrgModuleEntity;
import org.dows.hep.entity.CaseOrgModuleFuncRefEntity;
import org.dows.hep.service.CaseOrgModuleFuncRefService;
import org.dows.hep.service.CaseOrgModuleService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseOrgModuleBiz {
  private final CaseOrgModuleService caseOrgModuleService;
  private final CaseOrgModuleFuncRefService caseOrgModuleFuncRefService;
  private final IdGenerator idGenerator;
  @Transactional(rollbackFor = Exception.class)
  public void batchCreateOrUpdate(BatchCreateOrUpdateCaseOrgModuleRequestRs batchCreateOrUpdateCaseOrgModuleRequestRs) {
    List<CaseOrgModuleEntity> caseOrgModuleEntityList = new ArrayList<>();
    List<CaseOrgModuleFuncRefEntity> caseOrgModuleFuncRefEntityList = new ArrayList<>();
    String caseOrgId = batchCreateOrUpdateCaseOrgModuleRequestRs.getCaseOrgId();
    List<CreateOrUpdateCaseOrgModuleRequestRs> createOrUpdateCaseOrgModuleRequestRsList = batchCreateOrUpdateCaseOrgModuleRequestRs.getCreateOrUpdateCaseOrgModuleRequestRsList();
    Set<String> paramCaseOrgModuleIdSet = new HashSet<>();
    Set<String> dbCaseOrgModuleIdSet = new HashSet<>();
    Map<String, CaseOrgModuleEntity> kCaseOrgModuleIdVCaseOrgModuleEntityMap = new HashMap<>();
    Set<String> paramCaseOrgModuleFuncRefIdSet = new HashSet<>();
    Set<String> dbCaseOrgModuleFuncRefIdSet = new HashSet<>();
    Map<String, CaseOrgModuleFuncRefEntity> kCaseOrgModuleFuncRefIdVCaseOrgModuleFuncRefEntityMap = new HashMap<>();
    createOrUpdateCaseOrgModuleRequestRsList.forEach(createOrUpdateCaseOrgModuleRequestRs -> {
      String caseOrgModuleId = createOrUpdateCaseOrgModuleRequestRs.getCaseOrgModuleId();
      if (StringUtils.isNotBlank(caseOrgModuleId)) {
        paramCaseOrgModuleIdSet.add(caseOrgModuleId);
      }
      List<String> caseOrgModuleFuncRefIdList = createOrUpdateCaseOrgModuleRequestRs.getCaseOrgModuleFuncRefIdList();
      if (Objects.nonNull(caseOrgModuleFuncRefIdList) && !caseOrgModuleFuncRefIdList.isEmpty()) {
        paramCaseOrgModuleFuncRefIdSet.addAll(caseOrgModuleFuncRefIdList);
      }
    });
    if (!paramCaseOrgModuleIdSet.isEmpty()) {
      caseOrgModuleService.lambdaQuery()
          .in(CaseOrgModuleEntity::getCaseOrgModuleId, paramCaseOrgModuleIdSet)
          .list()
          .forEach(caseOrgModuleEntity -> {
            dbCaseOrgModuleIdSet.add(caseOrgModuleEntity.getCaseOrgModuleId());
            kCaseOrgModuleIdVCaseOrgModuleEntityMap.put(caseOrgModuleEntity.getCaseOrgModuleId(), caseOrgModuleEntity);
          });
      if (paramCaseOrgModuleIdSet.stream().anyMatch(caseOrgModuleId -> !dbCaseOrgModuleIdSet.contains(caseOrgModuleId))) {
        log.warn("CaseOrgModuleBiz.batchCreateOrUpdate param paramCaseOrgModuleIdSet:{} is illegal", paramCaseOrgModuleIdSet);
        throw new CaseOrgModuleException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    if (!paramCaseOrgModuleFuncRefIdSet.isEmpty()) {
      caseOrgModuleFuncRefService.lambdaQuery()
          .in(CaseOrgModuleFuncRefEntity::getCaseOrgModuleFuncRefId, paramCaseOrgModuleFuncRefIdSet)
          .list()
          .forEach(caseOrgModuleFuncRefEntity -> {
            dbCaseOrgModuleFuncRefIdSet.add(caseOrgModuleFuncRefEntity.getCaseOrgModuleFuncRefId());
            kCaseOrgModuleFuncRefIdVCaseOrgModuleFuncRefEntityMap.put(caseOrgModuleFuncRefEntity.getCaseOrgModuleFuncRefId(), caseOrgModuleFuncRefEntity);
          });
      if (paramCaseOrgModuleFuncRefIdSet.stream().anyMatch(caseOrgModuleFuncRefId -> !dbCaseOrgModuleFuncRefIdSet.contains(caseOrgModuleFuncRefId))) {
        log.warn("CaseOrgModuleBiz.batchCreateOrUpdate param paramCaseOrgModuleFuncRefIdSet:{} is illegal", paramCaseOrgModuleFuncRefIdSet);
        throw new CaseOrgModuleException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    createOrUpdateCaseOrgModuleRequestRsList.forEach(createOrUpdateCaseOrgModuleRequestRs -> {
      CaseOrgModuleEntity caseOrgModuleEntity = null;
      String caseOrgModuleId = createOrUpdateCaseOrgModuleRequestRs.getCaseOrgModuleId();
      String name = createOrUpdateCaseOrgModuleRequestRs.getName();
      if (StringUtils.isBlank(caseOrgModuleId)) {
        caseOrgModuleId = idGenerator.nextIdStr();
        caseOrgModuleEntity = CaseOrgModuleEntity
            .builder()
            .caseOrgModuleId(caseOrgModuleId)
            .appId(caseOrgId)
            .name(name)
            .build();
      } else {
        caseOrgModuleEntity = kCaseOrgModuleIdVCaseOrgModuleEntityMap.get(caseOrgModuleId);
        if (Objects.isNull(caseOrgModuleEntity)) {
          log.warn("CaseOrgModuleBiz.batchCreateOrUpdate param caseOrgModuleId:{} is illegal", caseOrgModuleId);
          throw new CaseOrgModuleException(EnumESC.VALIDATE_EXCEPTION);
        }
        caseOrgModuleEntity.setName(name);
      }
      caseOrgModuleEntityList.add(caseOrgModuleEntity);
      List<String> caseOrgModuleFuncRefIdList = createOrUpdateCaseOrgModuleRequestRs.getCaseOrgModuleFuncRefIdList();
      if (Objects.isNull(caseOrgModuleFuncRefIdList)) {
        return;
      }
      caseOrgModuleFuncRefIdList.forEach(caseOrgModuleFuncRefId -> {
        CaseOrgModuleFuncRefEntity caseOrgModuleFuncRefEntity = kCaseOrgModuleFuncRefIdVCaseOrgModuleFuncRefEntityMap.get(caseOrgModuleFuncRefId);
        if (Objects.isNull(caseOrgModuleFuncRefEntity)) {
          /* runsix:TODO  */
          caseOrgModuleFuncRefEntity = CaseOrgModuleFuncRefEntity
              .builder()

              .build();
        }
      });
    });
    caseOrgModuleService.saveOrUpdateBatch(caseOrgModuleEntityList);
    caseOrgModuleFuncRefService.saveOrUpdateBatch(caseOrgModuleFuncRefEntityList);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseOrgModuleId) {
  }


  public List<CaseOrgModuleResponseRs> getByCaseOrgId(String appId, String caseOrgId) {
    return null;
  }
}
