package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateCaseOrgModuleRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateCaseOrgModuleFuncRefRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateCaseOrgModuleRequestRs;
import org.dows.hep.api.base.indicator.response.CaseOrgModuleFuncRefResponseRs;
import org.dows.hep.api.base.indicator.response.CaseOrgModuleResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.CaseOrgModuleException;
import org.dows.hep.entity.CaseOrgModuleEntity;
import org.dows.hep.entity.CaseOrgModuleFuncRefEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.service.CaseOrgModuleFuncRefService;
import org.dows.hep.service.CaseOrgModuleService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
  private final IndicatorFuncService indicatorFuncService;
  private final CaseOrgModuleFuncRefBiz caseOrgModuleFuncRefBiz;
  public static CaseOrgModuleResponseRs caseOrgModule2ResponseRs(
      CaseOrgModuleEntity caseOrgModuleEntity,
      List<CaseOrgModuleFuncRefResponseRs> caseOrgModuleFuncRefResponseRsList
  ) {
    if (Objects.isNull(caseOrgModuleEntity)) {
      return null;
    }
    return CaseOrgModuleResponseRs
        .builder()
        .caseOrgModuleId(caseOrgModuleEntity.getCaseOrgModuleId())
        .appId(caseOrgModuleEntity.getAppId())
        .name(caseOrgModuleEntity.getName())
        .caseOrgModuleFuncRefResponseRsList(caseOrgModuleFuncRefResponseRsList)
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void batchCreateOrUpdate(BatchCreateOrUpdateCaseOrgModuleRequestRs batchCreateOrUpdateCaseOrgModuleRequestRs) {
    List<CaseOrgModuleEntity> caseOrgModuleEntityList = new ArrayList<>();
    List<CaseOrgModuleFuncRefEntity> caseOrgModuleFuncRefEntityList = new ArrayList<>();
    String appId = batchCreateOrUpdateCaseOrgModuleRequestRs.getAppId();
    String caseOrgId = batchCreateOrUpdateCaseOrgModuleRequestRs.getCaseOrgId();
    List<CreateOrUpdateCaseOrgModuleRequestRs> createOrUpdateCaseOrgModuleRequestRsList = batchCreateOrUpdateCaseOrgModuleRequestRs.getCreateOrUpdateCaseOrgModuleRequestRsList();
    Set<String> paramCaseOrgModuleIdSet = new HashSet<>();
    Set<String> dbCaseOrgModuleIdSet = new HashSet<>();
    Map<String, CaseOrgModuleEntity> kCaseOrgModuleIdVCaseOrgModuleEntityMap = new HashMap<>();
    Set<String> paramCaseOrgModuleFuncRefIdSet = new HashSet<>();
    Set<String> dbCaseOrgModuleFuncRefIdSet = new HashSet<>();
    Map<String, CaseOrgModuleFuncRefEntity> kCaseOrgModuleFuncRefIdVCaseOrgModuleFuncRefEntityMap = new HashMap<>();
    Set<String> paramIndicatorFuncIdSet = new HashSet<>();
    Set<String> dbIndicatorFuncIdSet = new HashSet<>();
    createOrUpdateCaseOrgModuleRequestRsList.forEach(createOrUpdateCaseOrgModuleRequestRs -> {
      String caseOrgModuleId = createOrUpdateCaseOrgModuleRequestRs.getCaseOrgModuleId();
      if (StringUtils.isNotBlank(caseOrgModuleId)) {
        paramCaseOrgModuleIdSet.add(caseOrgModuleId);
      }
      List<CreateOrUpdateCaseOrgModuleFuncRefRequestRs> createOrUpdateCaseOrgModuleFuncRefRequestRsList = createOrUpdateCaseOrgModuleRequestRs.getCreateOrUpdateCaseOrgModuleFuncRefRequestRsList();
      if (Objects.isNull(createOrUpdateCaseOrgModuleFuncRefRequestRsList) || createOrUpdateCaseOrgModuleFuncRefRequestRsList.isEmpty()) {
        return;
      }
      createOrUpdateCaseOrgModuleFuncRefRequestRsList.forEach(createOrUpdateCaseOrgModuleFuncRefRequestRs -> {
        String caseOrgModuleFuncRefId = createOrUpdateCaseOrgModuleFuncRefRequestRs.getCaseOrgModuleFuncRefId();
        if (StringUtils.isNotBlank(caseOrgModuleFuncRefId)) {
          paramCaseOrgModuleFuncRefIdSet.add(caseOrgModuleFuncRefId);
        }
        String indicatorFuncId = createOrUpdateCaseOrgModuleFuncRefRequestRs.getIndicatorFuncId();
        if (StringUtils.isBlank(indicatorFuncId)) {
          log.warn("CaseOrgModuleBiz.batchCreateOrUpdate param indicatorFuncId is blank is illegal");
          throw new CaseOrgModuleException(EnumESC.VALIDATE_EXCEPTION);
        }
        paramIndicatorFuncIdSet.add(indicatorFuncId);
      });
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
    if (!paramIndicatorFuncIdSet.isEmpty()) {
      indicatorFuncService.lambdaQuery()
          .in(IndicatorFuncEntity::getIndicatorFuncId, paramIndicatorFuncIdSet)
          .list()
          .forEach(indicatorFuncEntity -> {
            dbIndicatorFuncIdSet.add(indicatorFuncEntity.getIndicatorFuncId());
          });
      if (paramIndicatorFuncIdSet.stream().anyMatch(indicatorFuncId -> !dbIndicatorFuncIdSet.contains(indicatorFuncId))) {
        log.warn("CaseOrgModuleBiz.batchCreateOrUpdate param paramIndicatorFuncIdSet:{} is illegal", paramIndicatorFuncIdSet);
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
            .appId(appId)
            .caseOrgId(caseOrgId)
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
      List<CreateOrUpdateCaseOrgModuleFuncRefRequestRs> createOrUpdateCaseOrgModuleFuncRefRequestRsList = createOrUpdateCaseOrgModuleRequestRs.getCreateOrUpdateCaseOrgModuleFuncRefRequestRsList();
      if (Objects.isNull(createOrUpdateCaseOrgModuleFuncRefRequestRsList) || createOrUpdateCaseOrgModuleFuncRefRequestRsList.isEmpty()) {
        return;
      }
      String finalCaseOrgModuleId = caseOrgModuleId;
      createOrUpdateCaseOrgModuleFuncRefRequestRsList.forEach(createOrUpdateCaseOrgModuleFuncRefRequestRs -> {
        String caseOrgModuleFuncRefId = createOrUpdateCaseOrgModuleFuncRefRequestRs.getCaseOrgModuleFuncRefId();
        String indicatorFuncId = createOrUpdateCaseOrgModuleFuncRefRequestRs.getIndicatorFuncId();
        CaseOrgModuleFuncRefEntity caseOrgModuleFuncRefEntity = null;
        if (StringUtils.isBlank(caseOrgModuleFuncRefId)) {
          caseOrgModuleFuncRefId = idGenerator.nextIdStr();
          caseOrgModuleFuncRefEntity = CaseOrgModuleFuncRefEntity
              .builder()
              .caseOrgModuleFuncRefId(caseOrgModuleFuncRefId)
              .appId(appId)
              .caseOrgModuleId(finalCaseOrgModuleId)
              .indicatorFuncId(indicatorFuncId)
              .build();
        } else {
          CaseOrgModuleFuncRefEntity caseOrgModuleFuncRefEntity1 = kCaseOrgModuleFuncRefIdVCaseOrgModuleFuncRefEntityMap.get(caseOrgModuleFuncRefId);
          if (Objects.isNull(caseOrgModuleFuncRefEntity1)) {
            log.warn("CaseOrgModuleBiz.batchCreateOrUpdate param caseOrgModuleFuncRefId:{} is illegal", caseOrgModuleFuncRefId);
            throw new CaseOrgModuleException(EnumESC.VALIDATE_EXCEPTION);
          }
        }
        caseOrgModuleFuncRefEntityList.add(caseOrgModuleFuncRefEntity);
      });
    });
    caseOrgModuleService.saveOrUpdateBatch(caseOrgModuleEntityList);
    caseOrgModuleFuncRefService.saveOrUpdateBatch(caseOrgModuleFuncRefEntityList);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseOrgModuleId) {
    boolean isRemove = caseOrgModuleService.remove(
        new LambdaQueryWrapper<CaseOrgModuleEntity>()
            .eq(CaseOrgModuleEntity::getCaseOrgModuleId, caseOrgModuleId)
    );
    if (!isRemove) {
      log.warn("method CaseOrgModuleBiz.delete param caseOrgModuleId:{} is illegal", caseOrgModuleId);
      throw new CaseOrgModuleException(EnumESC.VALIDATE_EXCEPTION);
    }
    caseOrgModuleFuncRefService.remove(
        new LambdaQueryWrapper<CaseOrgModuleFuncRefEntity>()
            .eq(CaseOrgModuleFuncRefEntity::getCaseOrgModuleId, caseOrgModuleId)
    );
  }


  public List<CaseOrgModuleResponseRs> getByCaseOrgId(String appId, String caseOrgId) {
    List<CaseOrgModuleResponseRs> caseOrgModuleResponseRsList = new ArrayList<>();
    Set<String> caseOrgModuleIdSet = new HashSet<>();
    Map<String, List<CaseOrgModuleFuncRefResponseRs>> kCaseOrgModuleIdVCaseOrgModuleFuncRefResponseRsListMap = new HashMap<>();
    Map<String, List<CaseOrgModuleFuncRefEntity>> kCaseOrgModuleIdVCaseOrgModuleFuncRefEntityListMap = new HashMap<>();
    Set<String> indicatorFuncIdSet = new HashSet<>();
    Map<String, IndicatorFuncResponse> kIndicatorFuncIdVIndicatorFuncResponseMap = new HashMap<>();
    List<CaseOrgModuleEntity> caseOrgModuleEntityList = caseOrgModuleService.lambdaQuery()
        .eq(CaseOrgModuleEntity::getAppId, appId)
        .eq(CaseOrgModuleEntity::getCaseOrgId, caseOrgId)
        .list()
        .stream()
        .peek(caseOrgModuleEntity -> caseOrgModuleIdSet.add(caseOrgModuleEntity.getCaseOrgModuleId()))
        .collect(Collectors.toList());
    if (!caseOrgModuleIdSet.isEmpty()) {
      caseOrgModuleFuncRefService.lambdaQuery()
          .eq(CaseOrgModuleFuncRefEntity::getAppId, appId)
          .in(CaseOrgModuleFuncRefEntity::getCaseOrgModuleId, caseOrgModuleIdSet)
          .list()
          .forEach(caseOrgModuleFuncRefEntity -> {
            String indicatorFuncId = caseOrgModuleFuncRefEntity.getIndicatorFuncId();
            indicatorFuncIdSet.add(indicatorFuncId);
            String caseOrgModuleId = caseOrgModuleFuncRefEntity.getCaseOrgModuleId();
            List<CaseOrgModuleFuncRefEntity> caseOrgModuleFuncRefEntityList = kCaseOrgModuleIdVCaseOrgModuleFuncRefEntityListMap.get(caseOrgModuleId);
            if (Objects.isNull(caseOrgModuleFuncRefEntityList)) {
              caseOrgModuleFuncRefEntityList = new ArrayList<>();
            }
            caseOrgModuleFuncRefEntityList.add(caseOrgModuleFuncRefEntity);
            kCaseOrgModuleIdVCaseOrgModuleFuncRefEntityListMap.put(caseOrgModuleId, caseOrgModuleFuncRefEntityList);
          });
    }
    if (!indicatorFuncIdSet.isEmpty()) {
      indicatorFuncService.lambdaQuery()
          .eq(IndicatorFuncEntity::getAppId, appId)
          .in(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncIdSet)
          .list()
          .forEach(indicatorFuncEntity -> {
            IndicatorFuncResponse indicatorFuncResponse = IndicatorFuncBiz.indicatorFunc2Response(indicatorFuncEntity);
            kIndicatorFuncIdVIndicatorFuncResponseMap.put(indicatorFuncEntity.getIndicatorFuncId(), indicatorFuncResponse);
          });
    }
    kCaseOrgModuleIdVCaseOrgModuleFuncRefEntityListMap.forEach((caseOrgModuleId, caseOrgModuleFuncRefEntityList) -> {
      if (Objects.isNull(caseOrgModuleFuncRefEntityList)) {
        return;
      }
      caseOrgModuleFuncRefEntityList.forEach(caseOrgModuleFuncRefEntity -> {
        List<CaseOrgModuleFuncRefResponseRs> caseOrgModuleFuncRefResponseRsList = kCaseOrgModuleIdVCaseOrgModuleFuncRefResponseRsListMap.get(caseOrgModuleId);
        if (Objects.isNull(caseOrgModuleFuncRefResponseRsList)) {
          caseOrgModuleFuncRefResponseRsList = new ArrayList<>();
        }
        String indicatorFuncId = caseOrgModuleFuncRefEntity.getIndicatorFuncId();
        CaseOrgModuleFuncRefResponseRs caseOrgModuleFuncRefResponseRs = caseOrgModuleFuncRefBiz.caseOrgModuleFuncRef2ResponseRs(caseOrgModuleFuncRefEntity, kIndicatorFuncIdVIndicatorFuncResponseMap.get(indicatorFuncId));
        caseOrgModuleFuncRefResponseRsList.add(caseOrgModuleFuncRefResponseRs);
        kCaseOrgModuleIdVCaseOrgModuleFuncRefResponseRsListMap.put(caseOrgModuleId, caseOrgModuleFuncRefResponseRsList);
      });
    });
    caseOrgModuleEntityList.forEach(caseOrgModuleEntity -> {
      String caseOrgModuleId = caseOrgModuleEntity.getCaseOrgModuleId();
      List<CaseOrgModuleFuncRefResponseRs> caseOrgModuleFuncRefResponseRsList = kCaseOrgModuleIdVCaseOrgModuleFuncRefResponseRsListMap.get(caseOrgModuleId);
      CaseOrgModuleResponseRs caseOrgModuleResponseRs = CaseOrgModuleBiz.caseOrgModule2ResponseRs(caseOrgModuleEntity, caseOrgModuleFuncRefResponseRsList);
      caseOrgModuleResponseRsList.add(caseOrgModuleResponseRs);
    });
    return caseOrgModuleResponseRsList;
  }
}
