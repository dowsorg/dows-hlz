package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.IndicatorCategoryException;
import org.dows.hep.api.exception.IndicatorExpressionException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorExpressionBiz{

  @Value("${redisson.lock.lease-time.teacher.indicator-expression-create-delete-update:5000}")
  private Integer leaseTimeIndicatorExpressionCreateDeleteUpdate;

  private final RedissonClient redissonClient;

  private final String indicatorExpressionFieldAppId = "appId";
  private final IdGenerator idGenerator;
  private final IndicatorExpressionService indicatorExpressionService;
  private final IndicatorExpressionItemService indicatorExpressionItemService;
  private final IndicatorExpressionRefService indicatorExpressionRefService;

  private final IndicatorRuleService indicatorRuleService;
  public static IndicatorExpressionResponseRs indicatorExpression2ResponseRs(
      IndicatorExpressionEntity indicatorExpressionEntity,
      List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList,
      IndicatorExpressionItemResponseRs maxIndicatorExpressionItemResponseRs,
      IndicatorExpressionItemResponseRs minIndicatorExpressionItemResponseRs
      ) {
    if (Objects.isNull(indicatorExpressionEntity)) {
      return null;
    }
    if (Objects.isNull(indicatorExpressionItemResponseRsList)) {
      indicatorExpressionItemResponseRsList = new ArrayList<>();
    }
    return IndicatorExpressionResponseRs
        .builder()
        .id(indicatorExpressionEntity.getId())
        .indicatorExpressionId(indicatorExpressionEntity.getIndicatorExpressionId())
        .appId(indicatorExpressionEntity.getAppId())
        .type(indicatorExpressionEntity.getType())
        .deleted(indicatorExpressionEntity.getDeleted())
        .dt(indicatorExpressionEntity.getDt())
        .indicatorExpressionItemResponseRsList(indicatorExpressionItemResponseRsList)
        .maxIndicatorExpressionItemResponseRs(maxIndicatorExpressionItemResponseRs)
        .minIndicatorExpressionItemResponseRs(minIndicatorExpressionItemResponseRs)
        .build();
  }

  public void populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(String appId, Set<String> principalIdSet, Map<String, IndicatorExpressionResponseRs> kIndicatorInstanceIdVIndicatorExpressionResponseRsMap) {
    if (Objects.isNull(kIndicatorInstanceIdVIndicatorExpressionResponseRsMap)) {
      log.warn("method IndicatorInstanceBiz.populateKIndicatorExpressionIdVIndicatorExpressionEntityMap param kIndicatorInstanceIdVIndicatorExpressionResponseRsMap is null");
      return;
    }
    if (Objects.isNull(principalIdSet) || principalIdSet.isEmpty()) {
      return;
    }
    Map<String, String> kIndicatorInstanceIdVIndicatorExpressionIdMap = new HashMap<>();
    Set<String> indicatorExpressionIdSet = new HashSet<>();
    Map<String, IndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap = new HashMap<>();
    Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap = new HashMap<>();
    Set<String> maxAndMinIndicatorExpressionItemIdSet = new HashSet<>();
    Map<String, IndicatorExpressionItemResponseRs> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap = new HashMap<>();
    indicatorExpressionRefService.lambdaQuery()
        .eq(IndicatorExpressionRefEntity::getAppId, appId)
        .in(IndicatorExpressionRefEntity::getPrincipalId, principalIdSet)
        .list()
        .forEach(indicatorExpressionRefEntity -> {
          indicatorExpressionIdSet.add(indicatorExpressionRefEntity.getIndicatorExpressionId());
          kIndicatorInstanceIdVIndicatorExpressionIdMap.put(indicatorExpressionRefEntity.getPrincipalId(), indicatorExpressionRefEntity.getIndicatorExpressionId());
        });
    if (!indicatorExpressionIdSet.isEmpty()) {
      indicatorExpressionService.lambdaQuery()
          .eq(IndicatorExpressionEntity::getAppId, appId)
          .in(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionEntity -> {
            kIndicatorExpressionIdVIndicatorExpressionEntityMap.put(
                indicatorExpressionEntity.getIndicatorExpressionId(), indicatorExpressionEntity);
            String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
            String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
            }
            if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
            }
          });
      indicatorExpressionItemService.lambdaQuery()
          .eq(IndicatorExpressionItemEntity::getAppId, appId)
          .in(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> {
            String indicatorExpressionId = indicatorExpressionItemEntity.getIndicatorExpressionId();
            List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
            if (Objects.isNull(indicatorExpressionItemEntityList)) {
              indicatorExpressionItemEntityList = new ArrayList<>();
            }
            indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
            kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, indicatorExpressionItemEntityList);
          });
    }
    if (!maxAndMinIndicatorExpressionItemIdSet.isEmpty()) {
      indicatorExpressionItemService.lambdaQuery()
          .eq(IndicatorExpressionItemEntity::getAppId, appId)
          .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, maxAndMinIndicatorExpressionItemIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.put(
              indicatorExpressionItemEntity.getIndicatorExpressionItemId(), IndicatorExpressionItemBiz.indicatorExpressionItem2ResponseRs(indicatorExpressionItemEntity)
          ));
    }
    kIndicatorInstanceIdVIndicatorExpressionIdMap.forEach((indicatorInstanceId, indicatorExpressionId) -> {
      IndicatorExpressionEntity indicatorExpressionEntity = kIndicatorExpressionIdVIndicatorExpressionEntityMap.get(indicatorExpressionId);
      if (Objects.isNull(indicatorExpressionEntity)) {
        return;
      }
      String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
      String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
      if (Objects.isNull(indicatorExpressionItemEntityList)) {
        indicatorExpressionItemEntityList = new ArrayList<>();
      }
      List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionItemEntityList.stream().map(IndicatorExpressionItemBiz::indicatorExpressionItem2ResponseRs)
          .sorted(Comparator.comparingInt(IndicatorExpressionItemResponseRs::getSeq)).collect(Collectors.toList());
      IndicatorExpressionResponseRs indicatorExpressionResponseRs = IndicatorExpressionBiz.indicatorExpression2ResponseRs(
          indicatorExpressionEntity,
          indicatorExpressionItemResponseRsList,
          kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(maxIndicatorExpressionItemId),
          kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(minIndicatorExpressionItemId)
      );
      kIndicatorInstanceIdVIndicatorExpressionResponseRsMap.put(indicatorInstanceId, indicatorExpressionResponseRs);
    });
  }

  @Transactional(rollbackFor = Exception.class)
  public void createOrUpdate(CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) throws InterruptedException {
    String indicatorExpressionId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionId();
    String principalId = createOrUpdateIndicatorExpressionRequestRs.getPrincipalId();
    String indicatorExpressionRefId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionRefId();
    String reasonId = createOrUpdateIndicatorExpressionRequestRs.getReasonId();
    String appId = createOrUpdateIndicatorExpressionRequestRs.getAppId();
    Integer type = createOrUpdateIndicatorExpressionRequestRs.getType();
    Integer source = createOrUpdateIndicatorExpressionRequestRs.getSource();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_EXPRESSION_CREATE_DELETE_UPDATE, indicatorExpressionFieldAppId, appId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorExpressionCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorCategoryException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_CATEGORY_LATER);
    }
    try {
      IndicatorExpressionEntity indicatorExpressionEntity = null;
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = new ArrayList<>();
      List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList = createOrUpdateIndicatorExpressionRequestRs.getCreateOrUpdateIndicatorExpressionItemRequestRsList();
      Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap = new HashMap<>();
      Set<String> paramIndicatorExpressionItemIdSet = new HashSet<>();
      Set<String> dbIndicatorExpressionItemIdSet = new HashSet<>();
      CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs = createOrUpdateIndicatorExpressionRequestRs.getMaxCreateOrUpdateIndicatorExpressionItemRequestRs();
      CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs = createOrUpdateIndicatorExpressionRequestRs.getMinCreateOrUpdateIndicatorExpressionItemRequestRs();
      IndicatorExpressionRefEntity indicatorExpressionRefEntity = indicatorExpressionRefService.lambdaQuery()
          .eq(IndicatorExpressionRefEntity::getIndicatorExpressionRefId, indicatorExpressionRefId)
          .one();
      if (StringUtils.isBlank(indicatorExpressionId)) {
        indicatorExpressionId = idGenerator.nextIdStr();
        indicatorExpressionEntity = IndicatorExpressionEntity
            .builder()
            .indicatorExpressionId(indicatorExpressionId)
            .appId(appId)
            .type(type)
            .source(source)
            .build();
        indicatorExpressionRefEntity = IndicatorExpressionRefEntity
            .builder()
            .indicatorExpressionRefId(idGenerator.nextIdStr())
            .appId(appId)
            .indicatorExpressionId(indicatorExpressionId)
            .principalId(principalId)
            .reasonId(reasonId)
            .build();
      } else {
        if (Objects.isNull(indicatorExpressionRefEntity)) {
          log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs indicatorExpressionRefId:{} is illegal", indicatorExpressionRefId);
          throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
        } else {
         indicatorExpressionRefEntity.setPrincipalId(principalId);
        }
        if (!createOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()) {
          createOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
            String indicatorExpressionItemId = createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(indicatorExpressionItemId)) {
              paramIndicatorExpressionItemIdSet.add(indicatorExpressionItemId);
            }
          });
          if (!paramIndicatorExpressionItemIdSet.isEmpty()) {
            indicatorExpressionItemService.lambdaQuery()
                .eq(IndicatorExpressionItemEntity::getAppId, appId)
                .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, paramIndicatorExpressionItemIdSet)
                .list()
                .forEach(indicatorExpressionItemEntity -> {
                  String indicatorExpressionItemId = indicatorExpressionItemEntity.getIndicatorExpressionItemId();
                  dbIndicatorExpressionItemIdSet.add(indicatorExpressionItemId);
                  kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.put(indicatorExpressionItemId, indicatorExpressionItemEntity);
                });
            if (
                paramIndicatorExpressionItemIdSet.stream().anyMatch(indicatorExpressionItemId -> !dbIndicatorExpressionItemIdSet.contains(indicatorExpressionItemId))
            ) {
              log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
              throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
            }
          }
        }
      }
      String finalIndicatorExpressionId1 = indicatorExpressionId;
      createOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
        String indicatorExpressionItemId = createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
        String conditionRaw = createOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw();
        String conditionExpression = createOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression();
        conditionExpression = getConditionExpression(conditionExpression);
        String conditionNameList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList();
        String conditionValList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList();
        String resultRaw = createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
        String resultExpression = createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
        String resultNameList = createOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
        String resultValList = createOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
        Integer seq = createOrUpdateIndicatorExpressionItemRequestRs.getSeq();
        IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
        if (StringUtils.isBlank(indicatorExpressionItemId)) {
          indicatorExpressionItemId = idGenerator.nextIdStr();
          indicatorExpressionItemEntity = IndicatorExpressionItemEntity
              .builder()
              .indicatorExpressionItemId(indicatorExpressionItemId)
              .indicatorExpressionId(finalIndicatorExpressionId1)
              .appId(appId)
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
        } else {
          indicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(indicatorExpressionItemId);
          if (Objects.isNull(indicatorExpressionItemEntity)) {
            log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
            throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
          }
          indicatorExpressionItemEntity.setConditionRaw(conditionRaw);
          indicatorExpressionItemEntity.setConditionExpression(conditionExpression);
          indicatorExpressionItemEntity.setConditionNameList(conditionNameList);
          indicatorExpressionItemEntity.setConditionValList(conditionValList);
          indicatorExpressionItemEntity.setResultRaw(resultRaw);
          indicatorExpressionItemEntity.setResultExpression(resultExpression);
          indicatorExpressionItemEntity.setResultNameList(resultNameList);
          indicatorExpressionItemEntity.setResultValList(resultValList);
          indicatorExpressionItemEntity.setSeq(seq);
        }
        indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
      });
      if (Objects.nonNull(maxCreateOrUpdateIndicatorExpressionItemRequestRs)) {
        IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
        String indicatorExpressionItemId = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
        String resultRaw = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
        String resultExpression = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
        String resultNameList = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
        String resultValList = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
        if (StringUtils.isNotBlank(resultRaw) && StringUtils.isNotBlank(resultExpression) && StringUtils.isNotBlank(resultNameList) && StringUtils.isNotBlank(resultValList)) {
          if (StringUtils.isBlank(indicatorExpressionItemId)) {
            indicatorExpressionItemId = idGenerator.nextIdStr();
            indicatorExpressionItemEntity = IndicatorExpressionItemEntity
                .builder()
                .indicatorExpressionItemId(indicatorExpressionItemId)
                .appId(appId)
                .resultRaw(resultRaw)
                .resultExpression(resultExpression)
                .resultNameList(resultNameList)
                .resultValList(resultValList)
                .build();
          } else {
            String finalIndicatorExpressionItemId = indicatorExpressionItemId;
            indicatorExpressionItemEntity = indicatorExpressionItemService.lambdaQuery()
                .eq(IndicatorExpressionItemEntity::getAppId, appId)
                .eq(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemId)
                .oneOpt()
                .orElseThrow(() -> {
                  log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs indicatorExpressionItemId:{} is illegal", finalIndicatorExpressionItemId);
                  throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorExpressionItemEntity.setResultRaw(resultRaw);
            indicatorExpressionItemEntity.setResultExpression(resultExpression);
            indicatorExpressionItemEntity.setResultNameList(resultNameList);
            indicatorExpressionItemEntity.setResultValList(resultValList);
          }
          indicatorExpressionEntity.setMaxIndicatorExpressionItemId(indicatorExpressionItemId);
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
        }
      }
      if (Objects.nonNull(minCreateOrUpdateIndicatorExpressionItemRequestRs)) {
        IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
        String indicatorExpressionItemId = minCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
        String resultRaw = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
        String resultExpression = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
        String resultNameList = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
        String resultValList = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
        if (StringUtils.isNotBlank(resultRaw) && StringUtils.isNotBlank(resultExpression) && StringUtils.isNotBlank(resultNameList) && StringUtils.isNotBlank(resultValList)) {
          if (StringUtils.isBlank(indicatorExpressionItemId)) {
            indicatorExpressionItemId = idGenerator.nextIdStr();
            indicatorExpressionItemEntity = IndicatorExpressionItemEntity
                .builder()
                .indicatorExpressionItemId(indicatorExpressionItemId)
                .appId(appId)
                .resultRaw(resultRaw)
                .resultExpression(resultExpression)
                .resultNameList(resultNameList)
                .resultValList(resultValList)
                .build();
          } else {
            String finalIndicatorExpressionItemId = indicatorExpressionItemId;
            indicatorExpressionItemEntity = indicatorExpressionItemService.lambdaQuery()
                .eq(IndicatorExpressionItemEntity::getAppId, appId)
                .eq(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemId)
                .oneOpt()
                .orElseThrow(() -> {
                  log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs indicatorExpressionItemId:{} is illegal", finalIndicatorExpressionItemId);
                  throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorExpressionItemEntity.setResultRaw(resultRaw);
            indicatorExpressionItemEntity.setResultExpression(resultExpression);
            indicatorExpressionItemEntity.setResultNameList(resultNameList);
            indicatorExpressionItemEntity.setResultValList(resultValList);
          }
          indicatorExpressionEntity.setMinIndicatorExpressionItemId(indicatorExpressionItemId);
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
        }
      }
      indicatorExpressionRefService.saveOrUpdate(indicatorExpressionRefEntity);
      indicatorExpressionService.saveOrUpdate(indicatorExpressionEntity);
      indicatorExpressionItemService.saveOrUpdateBatch(indicatorExpressionItemEntityList);
      Set<String> previousIndicatorInstanceIdList = new HashSet<>();
      checkExpression(source, previousIndicatorInstanceIdList, principalId, indicatorExpressionItemEntityList);
    } finally {
      lock.unlock();
    }
  }

  public void checkExpression(Integer source, Set<String> previousIndicatorInstanceIdList, String principalId, List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList) {
    indicatorExpressionItemEntityList.forEach(indicatorExpressionItemEntity -> {
      String conditionRaw = indicatorExpressionItemEntity.getConditionRaw();
      String resultRaw = indicatorExpressionItemEntity.getResultRaw();
      if (StringUtils.isNotBlank(conditionRaw) && StringUtils.isBlank(resultRaw))  {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkExpression condition is not blank but result is blank");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      }
      checkConditionExpression(indicatorExpressionItemEntity);
      checkResultExpression(source, previousIndicatorInstanceIdList, principalId, indicatorExpressionItemEntity);
    });
  }

  /**
   * runsix method process
   * 1.must be true or false
  */
  private void checkConditionExpression(IndicatorExpressionItemEntity indicatorExpressionItemEntity) {
    String conditionNameList = indicatorExpressionItemEntity.getConditionNameList();
    String conditionValList = indicatorExpressionItemEntity.getConditionValList();
    if (StringUtils.isBlank(conditionNameList)) {
      if (StringUtils.isBlank(conditionValList)) {
        return;
      } else {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkConditionExpression name & val number is not same");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      }
    } else {
      if (StringUtils.isBlank(conditionValList)) {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkConditionExpression name & val number is not same");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      } else {
        // continue
      }
    }
    try {
      StandardEvaluationContext context = new StandardEvaluationContext();
      List<String> conditionNameSplitList = Arrays.stream(conditionNameList.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
      List<String> conditionValSplitList = Arrays.stream(conditionValList.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
      if (conditionNameSplitList.size() != conditionValSplitList.size()) {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkConditionExpression name & val number is not same");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      }
      Map<String, String> kIndicatorInstanceIdVValMap = new HashMap<>();
      indicatorRuleService.lambdaQuery()
          .in(IndicatorRuleEntity::getVariableId, conditionValSplitList)
          .list()
          .forEach(indicatorRuleEntity -> {
            kIndicatorInstanceIdVValMap.put(indicatorRuleEntity.getVariableId(), indicatorRuleEntity.getDef());
          });
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String val = kIndicatorInstanceIdVValMap.get(conditionValSplitList.get(i));
        boolean isValDigital = NumberUtils.isCreatable(val);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), Double.parseDouble(val));
        } else {
          val = wrapStrWithDoubleSingleQuotes(val);
          context.setVariable(conditionNameSplitList.get(i), val);
        }
      }
      String conditionExpression = indicatorExpressionItemEntity.getConditionExpression();
      ExpressionParser parser2 = new SpelExpressionParser();
      Expression expression = parser2.parseExpression(conditionExpression);
      String conditionExpressionResult = expression.getValue(context, String.class);
      if(!StringUtils.equalsIgnoreCase(conditionExpressionResult, EnumBoolean.TRUE.getCode().toString()) && !StringUtils.equalsIgnoreCase(conditionExpressionResult, EnumBoolean.FALSE.getCode().toString())) {
        log.warn("conditionExpressionResult:{}", conditionExpressionResult);
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkConditionExpression result is not boolean");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      }
    } catch (Exception e) {
      log.warn("method IndicatorExpressionBiz.createOrUpdate checkConditionExpression result is not boolean");
      throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
    }
  }

  /**
   * runsix method process
   * 1.can not appear itself & same periods
   * 2.can not circular dependency
   */
  public void checkResultExpression(Integer source, Set<String> previousIndicatorInstanceIdList, String principalId, IndicatorExpressionItemEntity indicatorExpressionItemEntity) {
    String resultNameList = indicatorExpressionItemEntity.getResultNameList();
    String resultValList = indicatorExpressionItemEntity.getResultValList();
    String[] resultNameArray = new String[]{};
    String[] resultValArray = new String[]{};
    if (StringUtils.isBlank(resultNameList)) {
      if (StringUtils.isBlank(resultValList)) {
        return;
      } else {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkResultExpression name & val number is not same");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      }
    } else {
      if (StringUtils.isBlank(resultValList)) {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkResultExpression name & val number is not same");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      } else {
        // continue
      }
    }
    /* runsix: can not ref same indicatorInstanceId with current periods for example: a = a$0 + 1 */
    resultNameArray = resultNameList.split(EnumString.COMMA.getStr());
    resultValArray = resultValList.split(EnumString.COMMA.getStr());
    if (EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getType().equals(source)) {
      for (int i = 0; i <= resultNameArray.length - 1; i++) {
        String[] resultNameSpiltArray = resultNameArray[i].split(EnumString.SPLIT_DOLLAR.getStr());
        if (StringUtils.equals(principalId, resultValArray[i]) && StringUtils.equals(resultNameSpiltArray[1], EnumString.ZERO.getStr())) {
          log.warn("method IndicatorExpressionBiz.createOrUpdate checkResultExpression can not ref same indicatorInstanceId with current periods");
          throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
        }
      }
    }
    /* runsix:can not circular dependency */
    for (int i = 0; i <= resultValArray.length-1; i++) {
      String[] resultNameSplitArray = resultNameArray[i].split(EnumString.SPLIT_DOLLAR.getStr());
      String indicatorInstanceId = resultValArray[i];
      String periods = resultNameSplitArray[1];
      if (periods.startsWith(EnumString.UNDERLINE.getStr())) {
        continue;
      }
      if (!periods.equals(EnumString.ZERO.getStr())) {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkResultExpression 目前只支持本期$0,上期$_1，其它情况等待后续扩展");
        throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
      }
      if (previousIndicatorInstanceIdList.contains(indicatorInstanceId)) {
        continue;
      }
      previousIndicatorInstanceIdList.add(indicatorInstanceId);
      IndicatorExpressionRefEntity indicatorExpressionRefEntity = indicatorExpressionRefService.lambdaQuery()
          .eq(IndicatorExpressionRefEntity::getPrincipalId, indicatorInstanceId)
          .one();
      if (Objects.isNull(indicatorExpressionRefEntity)) {
        continue;
      }
      String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
      IndicatorExpressionEntity indicatorExpressionEntity = indicatorExpressionService.lambdaQuery()
          .eq(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionId)
          .one();
      if (Objects.isNull(indicatorExpressionEntity)) {
        log.warn("method IndicatorExpressionBiz.createOrUpdate checkResultExpression indicatorExpressionId:{} is illegal", indicatorExpressionId);
        continue;
      }
      Integer source1 = indicatorExpressionEntity.getSource();
      String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
      String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
      Set<String> maxAndMinIndicatorExpressionItemIdSet = new HashSet<>();
      if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
        maxAndMinIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
      }
      if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
        maxAndMinIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
      }
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList1 = indicatorExpressionItemService.lambdaQuery()
          .eq(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionId)
          .list();
      if (!maxAndMinIndicatorExpressionItemIdSet.isEmpty()) {
        List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList2 = indicatorExpressionItemService.lambdaQuery()
            .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, maxAndMinIndicatorExpressionItemIdSet)
            .list();
        indicatorExpressionItemEntityList1.addAll(indicatorExpressionItemEntityList2);
      }
      checkExpression(source1, previousIndicatorInstanceIdList, indicatorInstanceId, indicatorExpressionItemEntityList1);
    }
  }
  private static String getConditionExpression(String conditionExpression) {
    List<String> strList = new ArrayList<>();
    for (int i = 0; i <= conditionExpression.length()-1;) {
      if (checkSpace(conditionExpression.substring(i, i+1))) {
        i++;
        continue;
      }
      if (checkMathSingleOperator(conditionExpression.substring(i, i+1))) {
        strList.add(conditionExpression.substring(i, i+1));
        i++;
        continue;
      }
      if (i < conditionExpression.length()-1) {
        if (checkMathDoubleOperator(conditionExpression.substring(i, i+2))) {
          strList.add(conditionExpression.substring(i, i+2));
          i += 2;
          continue;
        }
      }
      /* runsix:指标 */
      if (checkIndicator(conditionExpression.substring(i, i+1))) {
        boolean isComplete = false;
        for (int j = i+1; j <= conditionExpression.length()-1;) {
          if (checkSpace(conditionExpression.substring(j, j+1))) {
            isComplete = true;
            i += 2;
            break;
          }
          /* runsix:找到$ */
          if (checkDollar(conditionExpression.substring(j, j+1))) {
            isComplete = true;
            /* runsix:下划线，表明是上n期 */
            if (checkUnderline(conditionExpression.substring(j+1, j+2))) {
              strList.add(conditionExpression.substring(i, j+3));
              i = j+3;
              break;
            } else {
              /* runsix:不是下划线，表面是当前期 */
              strList.add(conditionExpression.substring(i, j+2));
              i = j+2;
              break;
            }
          } else {
            j++;
          }
        }
        if (!isComplete) {
          strList.add(conditionExpression.substring(i));
          i = conditionExpression.length()-1+1;
        }
      } else {
        /* runsix:如果不是数字，一定是字符串 */
        if (!checkNumber(conditionExpression.substring(i, i+1))) {
          boolean isComplete = false;
          for (int j = i+1; j <= conditionExpression.length()-1; j++) {
            if (checkSpace(conditionExpression.substring(j, j+1))) {
              isComplete = true;
              strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i, j)));
              i = j+1;
              break;
            }
            if (checkMathSingleOperator(conditionExpression.substring(j, j+1))) {
              isComplete = true;
              strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i, j)));
              strList.add(conditionExpression.substring(j, j+1));
              i = j+1;
              break;
            }
            if (j <= conditionExpression.length()-1-1) {
              if (checkMathDoubleOperator(conditionExpression.substring(j, j+2))) {
                isComplete = true;
                strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i, j+1)));
                strList.add(conditionExpression.substring(j, j+2));
                i = j+2;
                break;
              }
            }
          }
          if (!isComplete) {
            strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i)));
            i = conditionExpression.length()-1+1;
          }
        } else {
          boolean isComplete = false;
          boolean isNumber = true;
          /* runsix:如果是数字，看到最后有没有非数字 */
          for (int j = i+1; j <= conditionExpression.length()-1; j++) {
            if (checkSpace(conditionExpression.substring(j, j+1))) {
              if (isNumber) {
                strList.add(conditionExpression.substring(i, j));
              } else {
                strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i, j)));
              }
              isComplete = true;
              i = j+1;
              break;
            }
            if (checkMathSingleOperator(conditionExpression.substring(j, j+1))) {
              if (isNumber) {
                strList.add(conditionExpression.substring(i, j));
              } else {
                strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i, j)));
              }
              strList.add(conditionExpression.substring(j, j+1));
              isComplete = true;
              i = j+1;
              break;
            }
            if (j <= conditionExpression.length()-1-1) {
              if (checkMathDoubleOperator(conditionExpression.substring(j, j+2))) {
                if (isNumber) {
                  strList.add(conditionExpression.substring(i, j));
                } else {
                  strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i, j)));
                }
                strList.add(conditionExpression.substring(j, j+2));
                isComplete = true;
                i = j+2;
                break;
              }
            }
            /* runsix:如果不是数字，说明是字符串 */
            if (isNumber && !NumberUtils.isCreatable(conditionExpression.substring(j, j+1))) {
              isNumber = false;
            }
          }
          if (!isComplete) {
            if (isNumber) {
              strList.add(conditionExpression.substring(i));
            } else {
              strList.add(wrapStrWithDoubleSingleQuotes(conditionExpression.substring(i)));
            }
          }
        }
      }
    }
    return String.join(EnumString.SPACE.getStr(), strList);
  }
  private static String wrapStrWithDoubleSingleQuotes(String str) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    stringBuffer.append(str);
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    return stringBuffer.toString();
  }
  private static boolean checkNumber(String str) {
    return NumberUtils.isCreatable(str);
  }
  private static boolean checkIndicator(String str) {
    return str.startsWith(EnumString.JIN.getStr());
  }
  private static boolean checkMathSingleOperator(String str) {
    return StringUtils.equalsAnyIgnoreCase(str, "+", "-", "*", "/", "%", "(", ")");
  }
  private static boolean checkMathDoubleOperator(String str) {
    return StringUtils.equalsAnyIgnoreCase(str, ">=", "==", "<=", ">", "<");
  }
  private static boolean checkUnderline(String str) {
    return StringUtils.equalsIgnoreCase(str, EnumString.UNDERLINE.getStr());
  }
  private static boolean checkDollar(String str) {
    return StringUtils.equalsIgnoreCase(str, EnumString.SPLIT_DOLLAR.getStr());
  }
  private static boolean checkSpace(String str) {
    return StringUtils.equalsIgnoreCase(str, EnumString.SPACE.getStr());
  }

  /* runsix:TODO 需要删掉，测试使用 */
  public static void main(String[] args) {
//    String str = "#indicator0$_1 == 男";
//    System.out.println(getConditionExpression(str));
    String str = "indicator0@0";
    String[] split = str.split("@");
    System.out.println(split);
  }

  /* runsix:TODO */
  @Transactional(rollbackFor = Exception.class)
  public void batchBindReasonId(BatchBindReasonIdRequestRs batchBindReasonIdRequestRs) {
    String appId = batchBindReasonIdRequestRs.getAppId();
    String reasonId = batchBindReasonIdRequestRs.getReasonId();
    Integer source = batchBindReasonIdRequestRs.getSource();
    List<String> indicatorExpressionIdList = batchBindReasonIdRequestRs.getIndicatorExpressionIdList();

  }

  public void checkSourceAndReasonId(String source, String reasonId) {

  }
}
