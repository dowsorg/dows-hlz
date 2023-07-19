package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.CaseIndicatorExpressionBizException;
import org.dows.hep.api.exception.IndicatorExpressionException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorExpressionBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-expression-create-delete-update:5000}")
  private Integer leaseTimeCaseIndicatorExpressionCreateDeleteUpdate;
  private final RedissonClient redissonClient;
  private final String caseIndicatorExpressionFieldAppId = "appId";
  private final IdGenerator idGenerator;
  private final RsUtilBiz rsUtilBiz;
  private final RsCaseIndicatorExpressionBiz rsCaseIndicatorExpressionBiz;
  private final CaseIndicatorCategoryService caseIndicatorCategoryService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;

  public CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemResponseRs2Case(
      String caseIndicatorExpressionItemId,
      String indicatorExpressionItemId,
      String appId,
      String caseIndicatorExpressionId,
      String conditionRaw,
      String conditionExpression,
      String conditionNameList,
      String conditionValList,
      String resultRaw,
      String resultExpression,
      String resultNameList,
      String resultValList,
      Integer seq
  ) {
    return CaseIndicatorExpressionItemEntity
        .builder()
        .caseIndicatorExpressionItemId(caseIndicatorExpressionItemId)
        .indicatorExpressionItemId(indicatorExpressionItemId)
        .appId(appId)
        .indicatorExpressionId(caseIndicatorExpressionId)
        .conditionRaw(conditionRaw)
        .conditionExpression(conditionExpression)
        .conditionNameList(conditionNameList)
        .conditionValList(conditionValList)
        .resultRaw(resultRaw)
        .resultExpression(resultExpression)
        .resultNameList(resultNameList)
        .resultValList(resultValList)
        .seq(seq)
        .build();
  }

  public static CaseIndicatorExpressionResponseRs caseIndicatorExpression2ResponseRs(
      CaseIndicatorExpressionEntity caseIndicatorExpressionEntity,
      List<CaseIndicatorExpressionItemResponseRs> caseIndicatorExpressionItemResponseRsList,
      CaseIndicatorExpressionItemResponseRs caseMaxIndicatorExpressionItemResponseRs,
      CaseIndicatorExpressionItemResponseRs caseMinIndicatorExpressionItemResponseRs,
      CaseIndicatorCategoryResponse caseIndicatorCategoryResponse,
      String caseIndicatorExpressionRefId
  ) {
    if (Objects.isNull(caseIndicatorExpressionEntity)) {
      return null;
    }
    if (Objects.isNull(caseIndicatorExpressionItemResponseRsList)) {
      caseIndicatorExpressionItemResponseRsList = new ArrayList<>();
    }
    return CaseIndicatorExpressionResponseRs
        .builder()
        .id(caseIndicatorExpressionEntity.getId())
        .indicatorExpressionRefId(caseIndicatorExpressionRefId)
        .indicatorExpressionId(caseIndicatorExpressionEntity.getIndicatorExpressionId())
        .appId(caseIndicatorExpressionEntity.getAppId())
        .principalId(caseIndicatorExpressionEntity.getPrincipalId())
        .caseIndicatorCategoryResponse(caseIndicatorCategoryResponse)
        .type(caseIndicatorExpressionEntity.getType())
        .source(caseIndicatorExpressionEntity.getSource())
        .deleted(caseIndicatorExpressionEntity.getDeleted())
        .dt(caseIndicatorExpressionEntity.getDt())
        .caseIndicatorExpressionItemResponseRsList(caseIndicatorExpressionItemResponseRsList)
        .caseMaxIndicatorExpressionItemResponseRs(caseMaxIndicatorExpressionItemResponseRs)
        .caseMinIndicatorExpressionItemResponseRs(caseMinIndicatorExpressionItemResponseRs)
        .build();
  }

  public void populateKCaseReasonIdVCaseIndicatorExpressionResponseRsListMap(String appId, Set<String> reasonIdSet, Map<String, List<CaseIndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap) {
    if (Objects.isNull(kReasonIdVIndicatorExpressionResponseRsListMap)) {
      log.warn("method IndicatorExpressionBiz.populateKIndicatorExpressionIdVIndicatorExpressionEntityMap param kIndicatorInstanceIdVIndicatorExpressionResponseRsMap is null");
      return;
    }
    if (Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()) {
      return;
    }
    Map<String, List<String>> kReasonIdVIndicatorExpressionIdListMap = new HashMap<>();
    Map<String, String> kIndicatorExpressionIdVIndicatorExpressionRefIdMap = new HashMap<>();
    Set<String> indicatorExpressionIdSet = new HashSet<>();
    caseIndicatorExpressionRefService.lambdaQuery()
        .eq(CaseIndicatorExpressionRefEntity::getAppId, appId)
        .in(CaseIndicatorExpressionRefEntity::getReasonId, reasonIdSet)
        .list()
        .forEach(indicatorExpressionRefEntity -> {
          String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
          indicatorExpressionIdSet.add(indicatorExpressionId);
          String reasonId = indicatorExpressionRefEntity.getReasonId();
          List<String> indicatorExpressionIdList = kReasonIdVIndicatorExpressionIdListMap.get(reasonId);
          if (Objects.isNull(indicatorExpressionIdList)) {
            indicatorExpressionIdList = new ArrayList<>();
          }
          indicatorExpressionIdList.add(indicatorExpressionId);
          kReasonIdVIndicatorExpressionIdListMap.put(reasonId, indicatorExpressionIdList);
          kIndicatorExpressionIdVIndicatorExpressionRefIdMap.put(indicatorExpressionId, indicatorExpressionRefEntity.getIndicatorExpressionRefId());
        });
    Map<String, CaseIndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap = new HashMap<>();
    Map<String, List<CaseIndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap = new HashMap<>();
    Set<String> maxAndMinIndicatorExpressionItemIdSet = new HashSet<>();
    Map<String, CaseIndicatorExpressionItemResponseRs> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap = new HashMap<>();
    Set<String> principalIdSet = new HashSet<>();
    if (!indicatorExpressionIdSet.isEmpty()) {
      caseIndicatorExpressionService.lambdaQuery()
          .eq(CaseIndicatorExpressionEntity::getAppId, appId)
          .in(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionEntity -> {
            if (Objects.nonNull(indicatorExpressionEntity.getCasePrincipalId())) {
              principalIdSet.add(indicatorExpressionEntity.getCasePrincipalId());
            }
            kIndicatorExpressionIdVIndicatorExpressionEntityMap.put(
                indicatorExpressionEntity.getCaseIndicatorExpressionId(), indicatorExpressionEntity);
            String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
            String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
            }
            if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
            }
          });
      caseIndicatorExpressionItemService.lambdaQuery()
          .eq(CaseIndicatorExpressionItemEntity::getAppId, appId)
          .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> {
            String indicatorExpressionId = indicatorExpressionItemEntity.getIndicatorExpressionId();
            List<CaseIndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
            if (Objects.isNull(indicatorExpressionItemEntityList)) {
              indicatorExpressionItemEntityList = new ArrayList<>();
            }
            indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
            kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, indicatorExpressionItemEntityList);
          });
    }
    if (!maxAndMinIndicatorExpressionItemIdSet.isEmpty()) {
      caseIndicatorExpressionItemService.lambdaQuery()
          .eq(CaseIndicatorExpressionItemEntity::getAppId, appId)
          .in(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, maxAndMinIndicatorExpressionItemIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.put(
              indicatorExpressionItemEntity.getCaseIndicatorExpressionItemId(), CaseIndicatorExpressionItemBiz.caseIndicatorExpressionItem2ResponseRs(indicatorExpressionItemEntity)
          ));
    }
    Map<String, String> kIndicatorInstanceIdVIndicatorCategoryIdMap = new HashMap<>();
    Map<String, CaseIndicatorCategoryResponse> kPrincipalIdVIndicatorCategoryRsMap = new HashMap<>();
    Map<String, CaseIndicatorCategoryResponse> kIndicatorCategoryIdVIndicatorCategoryRsMap = new HashMap<>();
    Set<String> indicatorCategoryIdSet = new HashSet<>();
    if (!principalIdSet.isEmpty()) {
      caseIndicatorInstanceService.lambdaQuery()
          .eq(CaseIndicatorInstanceEntity::getAppId, appId)
          .in(CaseIndicatorInstanceEntity::getIndicatorInstanceId, principalIdSet)
          .list()
          .forEach(indicatorInstanceEntity -> {
            String indicatorCategoryId = indicatorInstanceEntity.getIndicatorCategoryId();
            indicatorCategoryIdSet.add(indicatorCategoryId);
            kIndicatorInstanceIdVIndicatorCategoryIdMap.put(indicatorInstanceEntity.getCaseIndicatorInstanceId(), indicatorInstanceEntity.getIndicatorCategoryId());
          });
      if (!indicatorCategoryIdSet.isEmpty()) {
        caseIndicatorCategoryService.lambdaQuery()
            .eq(CaseIndicatorCategoryEntity::getAppId, appId)
            .in(CaseIndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
            .list()
            .forEach(indicatorCategoryEntity -> {
              kIndicatorCategoryIdVIndicatorCategoryRsMap.put(
                  indicatorCategoryEntity.getIndicatorCategoryId(),
                  CaseIndicatorCategoryBiz.caseIndicatorCategoryEntity2Response(indicatorCategoryEntity));
            });
      }
    }
    kIndicatorInstanceIdVIndicatorCategoryIdMap.forEach((indicatorInstanceId, indicatorCategoryId) -> {
      CaseIndicatorCategoryResponse indicatorCategoryResponse = kIndicatorCategoryIdVIndicatorCategoryRsMap.get(indicatorCategoryId);
      kPrincipalIdVIndicatorCategoryRsMap.put(indicatorInstanceId, indicatorCategoryResponse);
    });
    kReasonIdVIndicatorExpressionIdListMap.forEach((reasonId, indicatorExpressionIdList) -> {
      indicatorExpressionIdList.forEach(indicatorExpressionId -> {
        CaseIndicatorExpressionEntity indicatorExpressionEntity = kIndicatorExpressionIdVIndicatorExpressionEntityMap.get(indicatorExpressionId);
        if (Objects.isNull(indicatorExpressionEntity)) {
          return;
        }
        String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
        String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
        List<CaseIndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
        if (Objects.isNull(indicatorExpressionItemEntityList)) {
          indicatorExpressionItemEntityList = new ArrayList<>();
        }
        List<CaseIndicatorExpressionItemResponseRs> caseIndicatorExpressionItemResponseRsList = indicatorExpressionItemEntityList.stream().map(CaseIndicatorExpressionItemBiz::caseIndicatorExpressionItem2ResponseRs)
            .sorted(Comparator.comparingInt(CaseIndicatorExpressionItemResponseRs::getSeq)).collect(Collectors.toList());
        /* runsix:TODO 弥补孙福聪那边实现不了，他必须要返回2个 */
        rsUtilBiz.specialHandleCaseIndicatorExpressionItemResponseRsList(caseIndicatorExpressionItemResponseRsList);
        CaseIndicatorExpressionResponseRs caseIndicatorExpressionResponseRs = CaseIndicatorExpressionBiz.caseIndicatorExpression2ResponseRs(
            indicatorExpressionEntity,
            caseIndicatorExpressionItemResponseRsList,
            kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(maxIndicatorExpressionItemId),
            kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(minIndicatorExpressionItemId),
            kPrincipalIdVIndicatorCategoryRsMap.get(indicatorExpressionEntity.getPrincipalId()),
            kIndicatorExpressionIdVIndicatorExpressionRefIdMap.get(indicatorExpressionId)
        );
        List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(reasonId);
        if (Objects.isNull(caseIndicatorExpressionResponseRsList)) {
          caseIndicatorExpressionResponseRsList = new ArrayList<>();
        }
        caseIndicatorExpressionResponseRsList.add(caseIndicatorExpressionResponseRs);
        kReasonIdVIndicatorExpressionResponseRsListMap.put(reasonId, caseIndicatorExpressionResponseRsList);
      });
    });
  }

  @Transactional(rollbackFor = Exception.class)
  public String v2CreateOrUpdate(CaseCreateOrUpdateIndicatorExpressionRequestRs caseCreateOrUpdateIndicatorExpressionRequestRs) throws InterruptedException, ExecutionException {
    /* runsix:param */
    AtomicReference<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityAtomicReference = new AtomicReference<>();
    String caseIndicatorExpressionId = caseCreateOrUpdateIndicatorExpressionRequestRs.getCaseIndicatorExpressionId();
    String casePrincipalId = caseCreateOrUpdateIndicatorExpressionRequestRs.getCasePrincipalId();
    /* runsix: TODO 这两个有什么用 */
    String caseIndicatorExpressionRefId = caseCreateOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionRefId();
    String caseReasonId = caseCreateOrUpdateIndicatorExpressionRequestRs.getReasonId();
    String appId = caseCreateOrUpdateIndicatorExpressionRequestRs.getAppId();
    Integer paramType = caseCreateOrUpdateIndicatorExpressionRequestRs.getType();
    Integer source = caseCreateOrUpdateIndicatorExpressionRequestRs.getSource();
    List<CaseCreateOrUpdateIndicatorExpressionItemRequestRs> caseCreateOrUpdateIndicatorExpressionItemRequestRsList = caseCreateOrUpdateIndicatorExpressionRequestRs.getCaseCreateOrUpdateIndicatorExpressionItemRequestRsList();
    CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMinCreateOrUpdateIndicatorExpressionItemRequestRs = caseCreateOrUpdateIndicatorExpressionRequestRs.getCaseMinCreateOrUpdateIndicatorExpressionItemRequestRs();
    CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs = caseCreateOrUpdateIndicatorExpressionRequestRs.getCaseMaxCreateOrUpdateIndicatorExpressionItemRequestRs();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.CASE_INDICATOR_EXPRESSION_CREATE_DELETE_UPDATE, caseIndicatorExpressionFieldAppId, appId));
    boolean isLocked = lock.tryLock(leaseTimeCaseIndicatorExpressionCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new CaseIndicatorExpressionBizException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_CASE_INDICATOR_EXPRESSION_LATER);
    }
    try {
      /* runsix:result */
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
      AtomicBoolean typeChangeAtomicBoolean = new AtomicBoolean(Boolean.FALSE);
      AtomicReference<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityAtomicReference = new AtomicReference<>();
      AtomicReference<CaseIndicatorExpressionItemEntity> caseMinIndicatorExpressionItemEntityAtomicReference = new AtomicReference<>();
      AtomicReference<CaseIndicatorExpressionItemEntity> caseMaxIndicatorExpressionItemEntityAtomicReference = new AtomicReference<>();

      /* runsix: 2.1 check caseIndicatorExpressionId */
      CompletableFuture<Void> cfPopulateCaseIndicatorExpression = CompletableFuture.runAsync(() -> rsCaseIndicatorExpressionBiz.populateCaseIndicatorExpressionEntity(caseIndicatorExpressionEntityAtomicReference, caseIndicatorExpressionId));
      cfPopulateCaseIndicatorExpression.get();
      CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = caseIndicatorExpressionEntityAtomicReference.get();
      Integer dbType = null;
      /* runsix:2.2 populate typeChangeAtomicBoolean  */
      if (Objects.nonNull(caseIndicatorExpressionEntity)) {
        dbType = caseIndicatorExpressionEntity.getType();
        if (!dbType.equals(paramType)) {typeChangeAtomicBoolean.set(Boolean.TRUE);}
      }

      /* runsix:2.2 populate caseIndicatorExpressionInfluenceEntityAtomicReference */
      CompletableFuture<Void> cfCheckCircleDependencyAndPopulateCaseIndicatorExpressionInfluenceEntity = CompletableFuture.runAsync(() -> {
        try {
          rsCaseIndicatorExpressionBiz.checkCircleDependencyAndPopulateCaseIndicatorExpressionInfluenceEntity(
              caseIndicatorExpressionInfluenceEntityAtomicReference,
              source,
              casePrincipalId,
              caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
              caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
              caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
          );
        } catch (Exception e) {
          throw new CaseIndicatorExpressionBizException(EnumESC.CASE_INDICATOR_EXPRESSION_CIRCLE_DEPENDENCY);
        }
      });
      cfCheckCircleDependencyAndPopulateCaseIndicatorExpressionInfluenceEntity.get();

      if (Objects.nonNull(caseIndicatorExpressionEntity)) {
        /* runsix:2.4 公式类型发生变化，原先上下限需要删除 */
        Set<String> minAndMaxCaseIndicatorExpressionItemIdSet = new HashSet<>();
        String dbMinCaseIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId();
        String dbMaxCaseIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId();
        if (typeChangeAtomicBoolean.get()) {
          if (StringUtils.isNotBlank(dbMinCaseIndicatorExpressionItemId)) {minAndMaxCaseIndicatorExpressionItemIdSet.add(dbMinCaseIndicatorExpressionItemId);}
          if (StringUtils.isNotBlank(dbMaxCaseIndicatorExpressionItemId)) {minAndMaxCaseIndicatorExpressionItemIdSet.add(dbMaxCaseIndicatorExpressionItemId);}
          if (!minAndMaxCaseIndicatorExpressionItemIdSet.isEmpty()) {
            caseIndicatorExpressionItemService.remove(new LambdaQueryWrapper<CaseIndicatorExpressionItemEntity>().in(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, minAndMaxCaseIndicatorExpressionItemIdSet));
          }
        }

        /* runsix:2.5 获取原来的上下限 */
        Map<String, CaseIndicatorExpressionItemEntity> kMinAndMaxCaseIndicatorExpressionItemIdVIndicatorExpressionItemMap = new HashMap<>();
        CompletableFuture<Void> cfMinAndMaxPopulateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = CompletableFuture.runAsync(() -> {
          rsCaseIndicatorExpressionBiz.populateByCaseItemIdSetKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap(
              kMinAndMaxCaseIndicatorExpressionItemIdVIndicatorExpressionItemMap, minAndMaxCaseIndicatorExpressionItemIdSet
          );
        });
        cfMinAndMaxPopulateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get();

        /* runsix:2.6 populate minCaseIndicatorExpressionItemEntityAtomicReference && maxCaseIndicatorExpressionItemEntityAtomicReference 创建新的上下限 */
        CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity = kMinAndMaxCaseIndicatorExpressionItemIdVIndicatorExpressionItemMap.get(dbMinCaseIndicatorExpressionItemId);
        if (Objects.nonNull(minCaseIndicatorExpressionItemEntity)) {
          caseMinIndicatorExpressionItemEntityAtomicReference.set(minCaseIndicatorExpressionItemEntity);
        }
        CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity = kMinAndMaxCaseIndicatorExpressionItemIdVIndicatorExpressionItemMap.get(dbMaxCaseIndicatorExpressionItemId);
        if (Objects.nonNull(maxCaseIndicatorExpressionItemEntity)) {
          caseMaxIndicatorExpressionItemEntityAtomicReference.set(maxCaseIndicatorExpressionItemEntity);
        }
        CompletableFuture<Void> cfPopulateMinAndMaxCaseIndicatorExpressionItem = CompletableFuture.runAsync(() -> {
          rsCaseIndicatorExpressionBiz.populateMinAndMaxCaseIndicatorExpressionItem(
              typeChangeAtomicBoolean.get(),
              caseMinIndicatorExpressionItemEntityAtomicReference,
              caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
              caseMaxIndicatorExpressionItemEntityAtomicReference,
              caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
          );
        });
        cfPopulateMinAndMaxCaseIndicatorExpressionItem.get();
      }

      /* runsix: 2.7 populate new indicatorExpressionEntityAtomicReference 判断是新建还是修改*/
      CaseIndicatorExpressionItemEntity newMinCaseIndicatorExpressionItemEntity = caseMinIndicatorExpressionItemEntityAtomicReference.get();
      CaseIndicatorExpressionItemEntity newMaxCaseIndicatorExpressionItemEntity = caseMaxIndicatorExpressionItemEntityAtomicReference.get();
      String newMinCaseIndicatorExpressionItemId = null;
      if (Objects.nonNull(newMinCaseIndicatorExpressionItemEntity)
          && StringUtils.isNotBlank(newMinCaseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId())) {
        newMinCaseIndicatorExpressionItemId = newMinCaseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId();
      }
      String newMaxIndicatorExpressionItemId = null;
      if (Objects.nonNull(newMaxCaseIndicatorExpressionItemEntity)
          && StringUtils.isNotBlank(newMaxCaseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId())) {
        newMaxIndicatorExpressionItemId = newMaxCaseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId();
      }
      /* runsix:2.7.1 新建 */
      if (Objects.isNull(caseIndicatorExpressionEntity)) {
        caseIndicatorExpressionEntity = CaseIndicatorExpressionEntity
            .builder()
            .caseIndicatorExpressionId(idGenerator.nextIdStr())
            .appId(appId)
            .principalId(casePrincipalId)
            .minIndicatorExpressionItemId(newMinCaseIndicatorExpressionItemId)
            .maxIndicatorExpressionItemId(newMaxIndicatorExpressionItemId)
            .type(paramType)
            .source(source)
            .build();
        caseIndicatorExpressionEntityAtomicReference.set(caseIndicatorExpressionEntity);
      } else {
        /* runsix:2.7.2 修改 */
        /**
         * runsix method process
         * 1.如果公式改变类型
         *   1.1 如果原来是条件，现在变成了随机
         *     1.1.1 删除所有条件细项
         *     1.1.2 如果存在，删除最小与最大（上面做了）
         *   1.2 如果原来是随机，现在变成了条件
         *     1.2.1 如果存在，删除最小与最大（上面做了）
         */
        if (EnumIndicatorExpressionType.CONDITION.getType().equals(dbType) && EnumIndicatorExpressionType.RANDOM.getType().equals(paramType)) {
          caseIndicatorExpressionItemService.remove(new LambdaQueryWrapper<CaseIndicatorExpressionItemEntity>().eq(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionId));
        }
        if (EnumIndicatorExpressionType.RANDOM.getType().equals(dbType) && EnumIndicatorExpressionType.CONDITION.getType().equals(paramType)) {
          /* runsix:do nothing */
        }
        caseIndicatorExpressionEntity.setType(paramType);
        caseIndicatorExpressionEntity.setMinIndicatorExpressionItemId(newMinCaseIndicatorExpressionItemId);
        caseIndicatorExpressionEntity.setMaxIndicatorExpressionItemId(newMaxIndicatorExpressionItemId);
        caseIndicatorExpressionEntityAtomicReference.set(caseIndicatorExpressionEntity);
      }

      /* runsix:2.8 populate caseIndicatorExpressionItemEntityList */
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = new HashMap<>();
      CompletableFuture<Void> cfPopulateByIdKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = CompletableFuture.runAsync(() -> {
        rsCaseIndicatorExpressionBiz.populateByCaseIdKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap(
            kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap, caseIndicatorExpressionEntityAtomicReference.get().getIndicatorExpressionId()
        );
      });
      cfPopulateByIdKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get();
      CompletableFuture<Void> cfPopulateByDbAndParamCaseIndicatorExpressionItemEntityList = CompletableFuture.runAsync(() -> {
        rsCaseIndicatorExpressionBiz.populateByDbAndParamCaseIndicatorExpressionItemEntityList(
            caseIndicatorExpressionEntityAtomicReference.get().getCaseIndicatorExpressionId(),
            caseIndicatorExpressionItemEntityList, kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap, caseCreateOrUpdateIndicatorExpressionItemRequestRsList
        );
      });
      cfPopulateByDbAndParamCaseIndicatorExpressionItemEntityList.get();

      /* runsix:2.9 populateKCaseIndicatorInstanceIdVValMap */
      Map<String, String> kCaseIndicatorInstanceIdVValMap = new HashMap<>();
      Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
      List<String> conditionValListList = new ArrayList<>();
      List<String> resultValListList = new ArrayList<>();
      rsCaseIndicatorExpressionBiz.populateCaseCreateOrUpdateIndicatorExpressionItemRequestRsList(
          conditionValListList,
          resultValListList,
          caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
          caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
          caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
      );
      caseIndicatorInstanceIdSet.addAll(conditionValListList);
      caseIndicatorInstanceIdSet.addAll(resultValListList);
      CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdVValMap = CompletableFuture.runAsync(() -> {
        rsCaseIndicatorExpressionBiz.populateKCaseIndicatorInstanceIdVValMap(kCaseIndicatorInstanceIdVValMap, caseIndicatorInstanceIdSet);
      });
      cfPopulateKCaseIndicatorInstanceIdVValMap.get();
      /* runsix:2.10 check caseIndicatorExpressionItemEntityList */
      if (!caseIndicatorExpressionItemEntityList.isEmpty()) {
        caseIndicatorExpressionItemEntityList.forEach(caseIndicatorExpressionItemEntity -> {
          rsUtilBiz.checkCondition(kCaseIndicatorInstanceIdVValMap, RsIndicatorExpressionCheckConditionRequest
              .builder()
              .source(EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource())
              .field(EnumIndicatorExpressionField.CASE.getField())
              .conditionRaw(caseIndicatorExpressionItemEntity.getConditionRaw())
              .conditionExpression(caseIndicatorExpressionItemEntity.getConditionExpression())
              .conditionNameList(caseIndicatorExpressionItemEntity.getConditionNameList())
              .conditionValList(caseIndicatorExpressionItemEntity.getConditionValList())
              .build());
          rsUtilBiz.checkResult(kCaseIndicatorInstanceIdVValMap, RsIndicatorExpressionCheckoutResultRequest
              .builder()
              .source(EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource())
              .field(EnumIndicatorExpressionField.CASE.getField())
              .resultRaw(caseIndicatorExpressionItemEntity.getResultRaw())
              .resultExpression(caseIndicatorExpressionItemEntity.getResultExpression())
              .resultNameList(caseIndicatorExpressionItemEntity.getResultNameList())
              .resultValList(caseIndicatorExpressionItemEntity.getResultValList())
              .build());
        });
      }

      if (Objects.nonNull(caseMinIndicatorExpressionItemEntityAtomicReference.get())) {caseIndicatorExpressionItemService.saveOrUpdate(caseMinIndicatorExpressionItemEntityAtomicReference.get());}
      if (Objects.nonNull(caseMaxIndicatorExpressionItemEntityAtomicReference.get())) {caseIndicatorExpressionItemService.saveOrUpdate(caseMaxIndicatorExpressionItemEntityAtomicReference.get());}
      if (Objects.nonNull(caseIndicatorExpressionEntityAtomicReference.get())) {caseIndicatorExpressionService.saveOrUpdate(caseIndicatorExpressionEntityAtomicReference.get());}
      if (!caseIndicatorExpressionItemEntityList.isEmpty()) {caseIndicatorExpressionItemService.saveOrUpdateBatch(caseIndicatorExpressionItemEntityList);}
      CaseIndicatorExpressionInfluenceEntity caseIndicatorExpressionInfluenceEntity = caseIndicatorExpressionInfluenceEntityAtomicReference.get();
      if (Objects.nonNull(caseIndicatorExpressionInfluenceEntity)) {caseIndicatorExpressionInfluenceService.saveOrUpdate(caseIndicatorExpressionInfluenceEntity);}
    } finally {
      lock.unlock();
    }
    return caseIndicatorExpressionEntityAtomicReference.get().getIndicatorExpressionId();
  }

  public CaseIndicatorExpressionResponseRs get(String caseIndicatorExpressionId) throws ExecutionException, InterruptedException {
    /* runsix:result */
    AtomicReference<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityAR = new AtomicReference<>();
    List<CaseIndicatorExpressionItemResponseRs> caseIndicatorExpressionItemResponseRsList = new ArrayList<>();
    CaseIndicatorExpressionItemResponseRs caseMaxIndicatorExpressionItemResponseRs = null;
    CaseIndicatorExpressionItemResponseRs caseMinIndicatorExpressionItemResponseRs = null;
    CaseIndicatorCategoryResponse caseIndicatorCategoryResponse = null;

    CompletableFuture<Void> cfPopulateCaseIndicatorExpressionEntity = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateCaseIndicatorExpressionEntity(caseIndicatorExpressionEntityAR, caseIndicatorExpressionId);
    });
    cfPopulateCaseIndicatorExpressionEntity.get();

    List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
    CompletableFuture<Void> cfPopulateCaseIndicatorExpressionItemEntityList = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateCaseIndicatorExpressionItemEntityList(caseIndicatorExpressionItemEntityList, caseIndicatorExpressionId);
    });
    cfPopulateCaseIndicatorExpressionItemEntityList.get();
    caseIndicatorExpressionItemEntityList.forEach(caseIndicatorExpressionItemEntity -> {
      CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs = CaseIndicatorExpressionItemBiz.caseIndicatorExpressionItem2ResponseRs(caseIndicatorExpressionItemEntity);
      if (Objects.nonNull(caseIndicatorExpressionItemResponseRs)) {
        caseIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs);
      }
    });
    /* runsix:特殊处理，前端处理不了，后端处理 */
    CompletableFuture<Void> cfSpecialHandleCaseIndicatorExpressionItemResponseRsList = CompletableFuture.runAsync(() -> {
      rsUtilBiz.specialHandleCaseIndicatorExpressionItemResponseRsList(caseIndicatorExpressionItemResponseRsList);
    });
    cfSpecialHandleCaseIndicatorExpressionItemResponseRsList.get();

    AtomicReference<CaseIndicatorExpressionItemEntity> minCaseIndicatorExpressionItemEntityAR = new AtomicReference<>();
    if (Objects.nonNull(caseIndicatorExpressionEntityAR.get()) && StringUtils.isNotBlank(caseIndicatorExpressionEntityAR.get().getMinIndicatorExpressionItemId())) {
      CompletableFuture<Void> cfMinPopulateCaseIndicatorExpressionItemEntity = CompletableFuture.runAsync(() -> {
        rsCaseIndicatorExpressionBiz.populateCaseIndicatorExpressionItemEntity(minCaseIndicatorExpressionItemEntityAR, caseIndicatorExpressionEntityAR.get().getMinIndicatorExpressionItemId());
      });
      cfMinPopulateCaseIndicatorExpressionItemEntity.get();
    }

    AtomicReference<CaseIndicatorExpressionItemEntity> maxCaseIndicatorExpressionItemEntityAR = new AtomicReference<>();
    if (Objects.nonNull(caseIndicatorExpressionEntityAR.get()) && StringUtils.isNotBlank(caseIndicatorExpressionEntityAR.get().getMaxIndicatorExpressionItemId())) {
      CompletableFuture<Void> cfMaxPopulateCaseIndicatorExpressionItemEntity = CompletableFuture.runAsync(() -> {
        rsCaseIndicatorExpressionBiz.populateCaseIndicatorExpressionItemEntity(maxCaseIndicatorExpressionItemEntityAR, caseIndicatorExpressionEntityAR.get().getMaxIndicatorExpressionItemId());
      });
      cfMaxPopulateCaseIndicatorExpressionItemEntity.get();
    }

    AtomicReference<String> casePrincipalIdAR = new AtomicReference<>();
    if (Objects.nonNull(caseIndicatorExpressionEntityAR.get()) && Objects.nonNull(caseIndicatorExpressionEntityAR.get().getCasePrincipalId())) {
      casePrincipalIdAR.set(caseIndicatorExpressionEntityAR.get().getCasePrincipalId());
    }
    AtomicReference<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityAR = new AtomicReference<>();
    AtomicReference<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityAR = new AtomicReference<>();
    CompletableFuture<Void> cfPopulateCaseIndicatorInstanceEntity = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateCaseIndicatorInstanceEntity(caseIndicatorInstanceEntityAR, casePrincipalIdAR.get());
    });
    cfPopulateCaseIndicatorInstanceEntity.get();
    CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceEntityAR.get();
    if (Objects.nonNull(caseIndicatorInstanceEntity)) {
      String indicatorCategoryId = caseIndicatorInstanceEntity.getIndicatorCategoryId();
      CompletableFuture<Void> cfPopulateCaseIndicatorCategoryEntity = CompletableFuture.runAsync(() -> {
        rsCaseIndicatorExpressionBiz.populateCaseIndicatorCategoryEntity(caseIndicatorCategoryEntityAR, indicatorCategoryId);
      });
      cfPopulateCaseIndicatorCategoryEntity.get();
    }

    AtomicReference<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityAR = new AtomicReference<>();
    CompletableFuture<Void> cfCaseIndicatorExpressionRefEntityAR = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateCaseIndicatorExpressionRefEntity(caseIndicatorExpressionRefEntityAR, caseIndicatorExpressionId);
    });
    cfCaseIndicatorExpressionRefEntityAR.get();
    String caseIndicatorExpressionRefId = null;
    if (Objects.nonNull(caseIndicatorExpressionRefEntityAR.get())) {
      caseIndicatorExpressionRefId = caseIndicatorExpressionRefEntityAR.get().getCaseIndicatorExpressionRefId();
    }

    return CaseIndicatorExpressionBiz.caseIndicatorExpression2ResponseRs(
        caseIndicatorExpressionEntityAR.get(),
        caseIndicatorExpressionItemResponseRsList,
        caseMaxIndicatorExpressionItemResponseRs,
        caseMinIndicatorExpressionItemResponseRs,
        caseIndicatorCategoryResponse,
        caseIndicatorExpressionRefId
    );
  }

  @Transactional(rollbackFor = Exception.class)
  public void batchBindReasonId(CaseBatchBindReasonIdRequestRs caseBatchBindReasonIdRequestRs) {

  }
}
