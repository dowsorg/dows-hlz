package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorViewBaseInfoException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @description project descr:指标:查看指标基本信息类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewBaseInfoBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
    private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
    private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorViewBaseInfoService indicatorViewBaseInfoService;
    private final IndicatorViewBaseInfoDescrService indicatorViewBaseInfoDescrService;
    private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;
    private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
    private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
    private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;
    private final IndicatorViewBaseInfoSingleService indicatorViewBaseInfoSingleService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorInstanceService indicatorInstanceService;

    private IndicatorViewBaseInfoResponseRs indicatorViewBaseInfo2ResponseRs(IndicatorViewBaseInfoEntity indicatorViewBaseInfoEntity) {
      if (Objects.isNull(indicatorViewBaseInfoEntity)) {
        return null;
      }
      Long id = indicatorViewBaseInfoEntity.getId();
      String dbIndicatorViewBaseInfoId = indicatorViewBaseInfoEntity.getIndicatorViewBaseInfoId();
      String appId = indicatorViewBaseInfoEntity.getAppId();
      String indicatorFuncId = indicatorViewBaseInfoEntity.getIndicatorFuncId();
      Set<String> indicatorInstanceIdSet = new HashSet<>();
      List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = indicatorViewBaseInfoDescrService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
          .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoId, dbIndicatorViewBaseInfoId)
          .list();
      Map<String, List<IndicatorViewBaseInfoDescrRefEntity>> kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap = new HashMap<>();
      if (!indicatorViewBaseInfoDescrEntityList.isEmpty()) {
        indicatorViewBaseInfoDescrRefService.lambdaQuery()
            .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
            .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescrEntityList
                .stream().map(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId).collect(Collectors.toSet()))
            .list()
            .forEach(indicatorViewBaseInfoDescrRefEntity -> {
              String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId();
              indicatorInstanceIdSet.add(indicatorViewBaseInfoDescrRefEntity.getIndicatorInstanceId());
              List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap.get(indicatorViewBaseInfoDescId);
              if (Objects.isNull(indicatorViewBaseInfoDescrRefEntityList)) {
                indicatorViewBaseInfoDescrRefEntityList = new ArrayList<>();
              }
              indicatorViewBaseInfoDescrRefEntityList.add(indicatorViewBaseInfoDescrRefEntity);
              kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap.put(indicatorViewBaseInfoDescId, indicatorViewBaseInfoDescrRefEntityList);
            });
      }
      List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList = indicatorViewBaseInfoMonitorService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
          .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoId, dbIndicatorViewBaseInfoId)
          .list();
      Map<String, List<IndicatorViewBaseInfoMonitorContentEntity>> kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap = new HashMap<>();
      Set<String> indicatorViewBaseInfoMonitorContentIdSet = new HashSet<>();
      Map<String, List<IndicatorViewBaseInfoMonitorContentRefEntity>> kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap = new HashMap<>();
      if (!indicatorViewBaseInfoMonitorEntityList.isEmpty()) {
        indicatorViewBaseInfoMonitorContentService.lambdaQuery()
            .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
            .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorEntityList
                .stream().map(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId).collect(Collectors.toSet()))
            .list()
            .forEach(indicatorViewBaseInfoMonitorContentEntity -> {
              indicatorViewBaseInfoMonitorContentIdSet.add(indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId());
              String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId();
              List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap.get(indicatorViewBaseInfoMonitorId);
              if (Objects.isNull(indicatorViewBaseInfoMonitorContentEntityList)) {
                indicatorViewBaseInfoMonitorContentEntityList = new ArrayList<>();
              }
              indicatorViewBaseInfoMonitorContentEntityList.add(indicatorViewBaseInfoMonitorContentEntity);
              kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap.put(indicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorContentEntityList);
            });
        if (!indicatorViewBaseInfoMonitorContentIdSet.isEmpty()) {
          indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
              .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
              .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentIdSet)
              .list()
              .forEach(indicatorViewBaseInfoMonitorContentRefEntity -> {
                indicatorInstanceIdSet.add(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorInstanceId());
                String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId();
                List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap.get(indicatorViewBaseInfoMonitorContentId);
                if (Objects.isNull(indicatorViewBaseInfoMonitorContentRefEntityList)) {
                  indicatorViewBaseInfoMonitorContentRefEntityList = new ArrayList<>();
                }
                indicatorViewBaseInfoMonitorContentRefEntityList.add(indicatorViewBaseInfoMonitorContentRefEntity);
                kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap.put(indicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentRefEntityList);
              });
        }
      }
      List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = indicatorViewBaseInfoSingleService.lambdaQuery()
          .eq(IndicatorViewBaseInfoSingleEntity::getAppId, appId)
          .eq(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoId, dbIndicatorViewBaseInfoId)
          .list();
      indicatorViewBaseInfoSingleEntityList.forEach(indicatorViewBaseInfoSingleEntity -> indicatorInstanceIdSet.add(indicatorViewBaseInfoSingleEntity.getIndicatorInstanceId()));
      Map<String, IndicatorInstanceResponseRs> kIndicatorInstanceIdVIndicatorInstanceResponseRsMap = new HashMap<>();
      if (!indicatorInstanceIdSet.isEmpty()) {
        indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getAppId, appId)
            .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
            .list()
            .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.put(
                indicatorInstanceEntity.getIndicatorInstanceId(), IndicatorInstanceBiz.indicatorInstance2ResponseRs(indicatorInstanceEntity, null)
            ));
      }
      List<IndicatorViewBaseInfoDescrResponseRs> indicatorViewBaseInfoDescrResponseRsList = new ArrayList<>();
      if (!indicatorViewBaseInfoDescrEntityList.isEmpty()) {
        indicatorViewBaseInfoDescrResponseRsList = indicatorViewBaseInfoDescrEntityList
            .stream()
            .map(indicatorViewBaseInfoDescrEntity -> {
              String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId();
              List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefList = kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap.get(indicatorViewBaseInfoDescId);
              if (Objects.isNull(indicatorViewBaseInfoDescrRefList)) {
                indicatorViewBaseInfoDescrRefList = new ArrayList<>();
              }
              return IndicatorViewBaseInfoDescrResponseRs
                  .builder()
                  .id(indicatorViewBaseInfoDescrEntity.getId())
                  .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescId)
                  .appId(indicatorViewBaseInfoDescrEntity.getAppId())
                  .indicatorViewBaseInfoId(indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoId())
                  .name(indicatorViewBaseInfoDescrEntity.getName())
                  .seq(indicatorViewBaseInfoDescrEntity.getSeq())
                  .indicatorViewBaseInfoDescrRefResponseRsList(indicatorViewBaseInfoDescrRefList
                      .stream()
                      .map(indicatorViewBaseInfoDescrRefEntity -> {
                        return IndicatorViewBaseInfoDescrRefResponseRs
                            .builder()
                            .id(indicatorViewBaseInfoDescrRefEntity.getId())
                            .indicatorViewBaseInfoDescRefId(indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescRefId())
                            .appId(indicatorViewBaseInfoDescrRefEntity.getAppId())
                            .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId())
                            .indicatorInstanceResponseRs(kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.get(indicatorViewBaseInfoDescrRefEntity.getIndicatorInstanceId()))
                            .seq(indicatorViewBaseInfoDescrRefEntity.getSeq())
                            .build();
                      })
                      .collect(Collectors.toList())
                  )
                  .build();
            })
            .collect(Collectors.toList());
      }
      List<IndicatorViewBaseInfoMonitorResponseRs> indicatorViewBaseInfoMonitorResponseRsList = new ArrayList<>();
      if (!indicatorViewBaseInfoMonitorEntityList.isEmpty()) {
        indicatorViewBaseInfoMonitorResponseRsList = indicatorViewBaseInfoMonitorEntityList
            .stream()
            .map(indicatorViewBaseInfoMonitorEntity -> {
              String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoMonitorId();
              List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap
                  .get(indicatorViewBaseInfoMonitorId);
              return IndicatorViewBaseInfoMonitorResponseRs
                  .builder()
                  .id(indicatorViewBaseInfoMonitorEntity.getId())
                  .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorId)
                  .appId(indicatorViewBaseInfoMonitorEntity.getAppId())
                  .indicatorViewBaseInfoId(indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId())
                  .name(indicatorViewBaseInfoMonitorEntity.getName())
                  .seq(indicatorViewBaseInfoMonitorEntity.getSeq())
                  .indicatorViewBaseInfoMonitorContentResponseRsList(indicatorViewBaseInfoMonitorContentEntityList
                      .stream()
                      .map(indicatorViewBaseInfoMonitorContentEntity -> {
                        String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId();
                        List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap
                            .get(indicatorViewBaseInfoMonitorContentId);
                        if (Objects.isNull(indicatorViewBaseInfoMonitorContentRefEntityList)) {
                          indicatorViewBaseInfoMonitorContentRefEntityList = new ArrayList<>();
                        }
                        return IndicatorViewBaseInfoMonitorContentResponseRs
                            .builder()
                            .id(indicatorViewBaseInfoMonitorContentEntity.getId())
                            .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentId)
                            .appId(indicatorViewBaseInfoMonitorContentEntity.getAppId())
                            .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId())
                            .name(indicatorViewBaseInfoMonitorContentEntity.getName())
                            .seq(indicatorViewBaseInfoMonitorContentEntity.getSeq())
                            .indicatorViewBaseInfoMonitorContentRefResponseRsList(indicatorViewBaseInfoMonitorContentRefEntityList
                                .stream()
                                .map(indicatorViewBaseInfoMonitorContentRefEntity -> {
                                  return IndicatorViewBaseInfoMonitorContentRefResponseRs
                                      .builder()
                                      .id(indicatorViewBaseInfoMonitorContentRefEntity.getId())
                                      .indicatorViewBaseInfoMonitorContentRefId(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentRefId())
                                      .appId(indicatorViewBaseInfoMonitorContentRefEntity.getAppId())
                                      .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId())
                                      .indicatorInstanceResponseRs(kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.get(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorInstanceId()))
                                      .seq(indicatorViewBaseInfoMonitorContentRefEntity.getSeq())
                                      .build();
                                })
                                .collect(Collectors.toList())
                            )
                            .build();
                      })
                      .collect(Collectors.toList())
                  )
                  .build();
            })
            .collect(Collectors.toList());
      }
      List<IndicatorViewBaseInfoSingleResponseRs> indicatorViewBaseInfoSingleResponseRsList = new ArrayList<>();
      if (!indicatorViewBaseInfoSingleEntityList.isEmpty()) {
        indicatorViewBaseInfoSingleResponseRsList = indicatorViewBaseInfoSingleEntityList
            .stream()
            .map(indicatorViewBaseInfoSingleEntity -> {
              return IndicatorViewBaseInfoSingleResponseRs
                  .builder()
                  .id(indicatorViewBaseInfoSingleEntity.getId())
                  .indicatorViewBaseInfoSingleId(indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoSingleId())
                  .appId(indicatorViewBaseInfoSingleEntity.getAppId())
                  .indicatorViewBaseInfoId(indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoId())
                  .indicatorInstanceResponseRs(kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.get(indicatorViewBaseInfoSingleEntity.getIndicatorInstanceId()))
                  .seq(indicatorViewBaseInfoSingleEntity.getSeq())
                  .build();
            })
            .collect(Collectors.toList());
      }
      return IndicatorViewBaseInfoResponseRs
          .builder()
          .id(id)
          .indicatorViewBaseInfoId(dbIndicatorViewBaseInfoId)
          .appId(appId)
          .indicatorFuncId(indicatorFuncId)
          .indicatorViewBaseInfoDescrResponseRsList(indicatorViewBaseInfoDescrResponseRsList)
          .indicatorViewBaseInfoMonitorResponseRsList(indicatorViewBaseInfoMonitorResponseRsList)
          .indicatorViewBaseInfoSingleResponseRsList(indicatorViewBaseInfoSingleResponseRsList)
          .build();
    }

  private void populateIndicatorViewBaseInfoDescrRelatedList(
      List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList, List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList,
      Set<String> paramIndicatorInstanceIdSet, String appId,
      List<CreateOrUpdateIndicatorViewBaseInfoDescrRs> createOrUpdateIndicatorViewBaseInfoDescrRsList, String indicatorViewBaseInfoId) {
    Set<String> paramIndicatorViewBaseInfoDescIdSet = new HashSet<>();
    Set<String> dbIndicatorViewBaseInfoDescIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoDescrEntity> kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrEntityMap = new HashMap<>();
    Set<String> paramIndicatorViewBaseInfoDescRefIdSet = new HashSet<>();
    Set<String> dbIndicatorViewBaseInfoDescRefIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoDescrRefEntity> kIndicatorViewBaseInfoDescRefIdVIndicatorViewBaseInfoDescrRefEntityMap = new HashMap<>();
    createOrUpdateIndicatorViewBaseInfoDescrRsList.forEach(
        createOrUpdateIndicatorViewBaseInfoDescrRs -> {
          String indicatorViewBaseInfoDescId = createOrUpdateIndicatorViewBaseInfoDescrRs.getIndicatorViewBaseInfoDescId();
          if (StringUtils.isNotBlank(indicatorViewBaseInfoDescId)) {
            paramIndicatorViewBaseInfoDescIdSet.add(indicatorViewBaseInfoDescId);
            createOrUpdateIndicatorViewBaseInfoDescrRs.getCreateOrUpdateIndicatorViewBaseInfoDescrRefRequestRsList()
                .forEach(createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs -> {
                  String indicatorViewBaseInfoDescRefId = createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs.getIndicatorViewBaseInfoDescRefId();
                  if (StringUtils.isNotBlank(indicatorViewBaseInfoDescRefId)) {
                    paramIndicatorViewBaseInfoDescRefIdSet.add(indicatorViewBaseInfoDescRefId);
                  }
                  paramIndicatorInstanceIdSet.add(createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs.getIndicatorInstanceId());
                });
          }
        });
    if (!paramIndicatorViewBaseInfoDescIdSet.isEmpty()) {
      indicatorViewBaseInfoDescrService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId, paramIndicatorViewBaseInfoDescIdSet)
          .list()
          .forEach(indicatorViewBaseInfoDescrEntity -> {
            String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId();
            dbIndicatorViewBaseInfoDescIdSet.add(indicatorViewBaseInfoDescId);
            kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrEntityMap.put(indicatorViewBaseInfoDescId, indicatorViewBaseInfoDescrEntity);
          });
      if (
          paramIndicatorViewBaseInfoDescIdSet.stream().anyMatch(indicatorViewBaseInfoDescrId -> !dbIndicatorViewBaseInfoDescIdSet.contains(indicatorViewBaseInfoDescrId))
      ) {
        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field createOrUpdateIndicatorViewBaseInfoDescrRsList indicatorViewBaseInfoDescId:{} is illegal", paramIndicatorViewBaseInfoDescIdSet);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    if (!paramIndicatorViewBaseInfoDescRefIdSet.isEmpty()) {
      indicatorViewBaseInfoDescrRefService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescRefId, paramIndicatorViewBaseInfoDescRefIdSet)
          .list()
          .forEach(indicatorViewBaseInfoDescrRefEntity -> {
            String indicatorViewBaseInfoDescRefId = indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescRefId();
            dbIndicatorViewBaseInfoDescRefIdSet.add(indicatorViewBaseInfoDescRefId);
            kIndicatorViewBaseInfoDescRefIdVIndicatorViewBaseInfoDescrRefEntityMap.put(indicatorViewBaseInfoDescRefId, indicatorViewBaseInfoDescrRefEntity);
          });
      if (
          paramIndicatorViewBaseInfoDescRefIdSet.stream().anyMatch(indicatorViewBaseInfoDescRefId -> !dbIndicatorViewBaseInfoDescRefIdSet.contains(indicatorViewBaseInfoDescRefId))
      ) {
        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field createOrUpdateIndicatorViewBaseInfoDescrRsList indicatorViewBaseInfoDescRefId:{} is illegal", paramIndicatorViewBaseInfoDescRefIdSet);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    createOrUpdateIndicatorViewBaseInfoDescrRsList.forEach(
        createOrUpdateIndicatorViewBaseInfoDescrRs -> {
          String indicatorViewBaseInfoDescId = createOrUpdateIndicatorViewBaseInfoDescrRs.getIndicatorViewBaseInfoDescId();
          String name = createOrUpdateIndicatorViewBaseInfoDescrRs.getName();
          Integer seq = createOrUpdateIndicatorViewBaseInfoDescrRs.getSeq();
          IndicatorViewBaseInfoDescrEntity indicatorViewBaseInfoDescrEntity = null;
          if (StringUtils.isBlank(indicatorViewBaseInfoDescId)) {
            indicatorViewBaseInfoDescId = idGenerator.nextIdStr();
            indicatorViewBaseInfoDescrEntity = IndicatorViewBaseInfoDescrEntity
                .builder()
                .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescId)
                .appId(appId)
                .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
                .name(name)
                .seq(seq)
                .build();
          } else {
            indicatorViewBaseInfoDescrEntity = kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrEntityMap.get(indicatorViewBaseInfoDescId);
            indicatorViewBaseInfoDescrEntity.setName(name);
            indicatorViewBaseInfoDescrEntity.setSeq(seq);
          }
          indicatorViewBaseInfoDescrEntityList.add(indicatorViewBaseInfoDescrEntity);
          String finalIndicatorViewBaseInfoDescId = indicatorViewBaseInfoDescId;
          createOrUpdateIndicatorViewBaseInfoDescrRs.getCreateOrUpdateIndicatorViewBaseInfoDescrRefRequestRsList()
              .forEach(createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs -> {
                IndicatorViewBaseInfoDescrRefEntity indicatorViewBaseInfoDescrRefEntity = null;
                String indicatorViewBaseInfoDescRefId = createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs.getIndicatorViewBaseInfoDescRefId();
                String indicatorInstanceId = createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs.getIndicatorInstanceId();
                Integer seq1 = createOrUpdateIndicatorViewBaseInfoDescrRefRequestRs.getSeq();
                if (StringUtils.isBlank(indicatorViewBaseInfoDescRefId)) {
                  indicatorViewBaseInfoDescRefId = idGenerator.nextIdStr();
                  indicatorViewBaseInfoDescrRefEntity = IndicatorViewBaseInfoDescrRefEntity
                      .builder()
                      .indicatorViewBaseInfoDescRefId(indicatorViewBaseInfoDescRefId)
                      .appId(appId)
                      .indicatorViewBaseInfoDescId(finalIndicatorViewBaseInfoDescId)
                      .indicatorInstanceId(indicatorInstanceId)
                      .seq(seq1)
                      .build();
                } else {
                  indicatorViewBaseInfoDescrRefEntity = kIndicatorViewBaseInfoDescRefIdVIndicatorViewBaseInfoDescrRefEntityMap.get(indicatorViewBaseInfoDescRefId);
                  indicatorViewBaseInfoDescrRefEntity.setIndicatorInstanceId(indicatorInstanceId);
                  indicatorViewBaseInfoDescrRefEntity.setSeq(seq1);
                }
                indicatorViewBaseInfoDescrRefEntityList.add(indicatorViewBaseInfoDescrRefEntity);
              });
        }
    );
  }

  private void populateIndicatorViewBaseInfoMonitorRelatedList(
      List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList, List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList,
      List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList, Set<String> paramIndicatorInstanceIdSet, String appId,
      List<CreateOrUpdateIndicatorViewBaseInfoMonitorRs> createOrUpdateIndicatorViewBaseInfoMonitorRsList, String indicatorViewBaseInfoId) {
    Set<String> paramIndicatorViewBaseInfoMonitorIdSet = new HashSet<>();
    Set<String> dbIndicatorViewBaseInfoMonitorIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoMonitorEntity> kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorEntityMap = new HashMap<>();
    Set<String> paramIndicatorViewBaseInfoMonitorContentIdSet = new HashSet<>();
    Set<String> dbIndicatorViewBaseInfoMonitorContentIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoMonitorContentEntity> kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentEntityMap = new HashMap<>();
    Set<String> paramIndicatorViewBaseInfoMonitorContentRefIdSet = new HashSet<>();
    Set<String> dbIndicatorViewBaseInfoMonitorContentRefIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoMonitorContentRefEntity> kIndicatorViewBaseInfoMonitorContentRefIdVIndicatorViewBaseInfoMonitorContentRefEntityMap = new HashMap<>();
    createOrUpdateIndicatorViewBaseInfoMonitorRsList.forEach(
        createOrUpdateIndicatorViewBaseInfoMonitorRs -> {
          String indicatorViewBaseInfoMonitorId = createOrUpdateIndicatorViewBaseInfoMonitorRs.getIndicatorViewBaseInfoMonitorId();
          if (StringUtils.isNotBlank(indicatorViewBaseInfoMonitorId)) {
            paramIndicatorViewBaseInfoMonitorIdSet.add(indicatorViewBaseInfoMonitorId);
            List<CreateOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs> createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList = createOrUpdateIndicatorViewBaseInfoMonitorRs.getCreateOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList();
            if (Objects.nonNull(createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList) && !createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList.isEmpty()) {
              createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList.forEach(createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs -> {
                String indicatorViewBaseInfoMonitorContentId = createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs.getIndicatorViewBaseInfoMonitorContentId();
                if (StringUtils.isNotBlank(indicatorViewBaseInfoMonitorContentId)) {
                  paramIndicatorViewBaseInfoMonitorContentIdSet.add(indicatorViewBaseInfoMonitorContentId);
                  List<CreateOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs> createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRsList = createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs.getCreateOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRsList();
                  if (Objects.nonNull(createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRsList) && !createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRsList.isEmpty()) {
                    createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRsList.forEach(createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs -> {
                      String indicatorInstanceId = createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs.getIndicatorInstanceId();
                      if (StringUtils.isNotBlank(indicatorInstanceId)) {
                        paramIndicatorInstanceIdSet.add(indicatorInstanceId);
                      }
                      String indicatorViewBaseInfoMonitorContentRefId = createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs.getIndicatorViewBaseInfoMonitorContentRefId();
                      if (StringUtils.isNotBlank(indicatorViewBaseInfoMonitorContentRefId)) {
                        paramIndicatorViewBaseInfoMonitorContentRefIdSet.add(indicatorViewBaseInfoMonitorContentRefId);
                      }
                    });
                  }
                }
              });
            }
          }
        }
    );
    if (!paramIndicatorViewBaseInfoMonitorIdSet.isEmpty()) {
      indicatorViewBaseInfoMonitorService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, paramIndicatorViewBaseInfoMonitorIdSet)
          .list()
          .forEach(indicatorViewBaseInfoMonitorEntity -> {
            String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoMonitorId();
            dbIndicatorViewBaseInfoMonitorIdSet.add(indicatorViewBaseInfoMonitorId);
            kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorEntityMap.put(indicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorEntity);
          });
      if (
          paramIndicatorViewBaseInfoMonitorIdSet.stream().anyMatch(indicatorViewBaseInfoMonitorId -> !dbIndicatorViewBaseInfoMonitorIdSet.contains(indicatorViewBaseInfoMonitorId))
      ) {
        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field createOrUpdateIndicatorViewBaseInfoMonitorRsList indicatorViewBaseInfoMonitorId:{} is illegal", paramIndicatorViewBaseInfoMonitorIdSet);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    if (!paramIndicatorViewBaseInfoMonitorContentIdSet.isEmpty()) {
      indicatorViewBaseInfoMonitorContentService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, paramIndicatorViewBaseInfoMonitorContentIdSet)
          .list()
          .forEach(indicatorViewBaseInfoMonitorContentEntity -> {
            String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId();
            dbIndicatorViewBaseInfoMonitorContentIdSet.add(indicatorViewBaseInfoMonitorContentId);
            kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentEntityMap.put(indicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentEntity);
          });
      if (
          paramIndicatorViewBaseInfoMonitorContentIdSet.stream().anyMatch(indicatorViewBaseInfoMonitorContentId -> !dbIndicatorViewBaseInfoMonitorContentIdSet.contains(indicatorViewBaseInfoMonitorContentId))
      ) {
        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field createOrUpdateIndicatorViewBaseInfoMonitorRsList indicatorViewBaseInfoMonitorContentId:{} is illegal", paramIndicatorViewBaseInfoMonitorContentIdSet);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    if (!paramIndicatorViewBaseInfoMonitorContentRefIdSet.isEmpty()) {
      indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentRefId, paramIndicatorViewBaseInfoMonitorContentRefIdSet)
          .list()
          .forEach(indicatorViewBaseInfoMonitorContentRefEntity -> {
            String indicatorViewBaseInfoMonitorContentRefId = indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentRefId();
            dbIndicatorViewBaseInfoMonitorContentRefIdSet.add(indicatorViewBaseInfoMonitorContentRefId);
            kIndicatorViewBaseInfoMonitorContentRefIdVIndicatorViewBaseInfoMonitorContentRefEntityMap.put(indicatorViewBaseInfoMonitorContentRefId, indicatorViewBaseInfoMonitorContentRefEntity);
          });
      if (
          paramIndicatorViewBaseInfoMonitorContentRefIdSet.stream().anyMatch(indicatorViewBaseInfoMonitorContentRefId -> !dbIndicatorViewBaseInfoMonitorContentRefIdSet.contains(indicatorViewBaseInfoMonitorContentRefId))
      ) {
        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field createOrUpdateIndicatorViewBaseInfoMonitorRsList indicatorViewBaseInfoMonitorContentRefId:{} is illegal", paramIndicatorViewBaseInfoMonitorContentRefIdSet);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    createOrUpdateIndicatorViewBaseInfoMonitorRsList.forEach(createOrUpdateIndicatorViewBaseInfoMonitorRs -> {
      IndicatorViewBaseInfoMonitorEntity indicatorViewBaseInfoMonitorEntity = null;
      String indicatorViewBaseInfoMonitorId = createOrUpdateIndicatorViewBaseInfoMonitorRs.getIndicatorViewBaseInfoMonitorId();
      String name = createOrUpdateIndicatorViewBaseInfoMonitorRs.getName();
      Integer seq = createOrUpdateIndicatorViewBaseInfoMonitorRs.getSeq();
      if (StringUtils.isBlank(indicatorViewBaseInfoMonitorId)) {
        indicatorViewBaseInfoMonitorId = idGenerator.nextIdStr();
        indicatorViewBaseInfoMonitorEntity = IndicatorViewBaseInfoMonitorEntity
            .builder()
            .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorId)
            .appId(appId)
            .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
            .name(name)
            .seq(seq)
            .build();
      } else {
        indicatorViewBaseInfoMonitorEntity = kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorEntityMap.get(indicatorViewBaseInfoMonitorId);
        indicatorViewBaseInfoMonitorEntity.setName(name);
        indicatorViewBaseInfoMonitorEntity.setSeq(seq);
      }
      indicatorViewBaseInfoMonitorEntityList.add(indicatorViewBaseInfoMonitorEntity);
      String finalIndicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorId;
      createOrUpdateIndicatorViewBaseInfoMonitorRs.getCreateOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList()
          .forEach(createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs -> {
            IndicatorViewBaseInfoMonitorContentEntity indicatorViewBaseInfoMonitorContentEntity = null;
            String indicatorViewBaseInfoMonitorContentId = createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs.getIndicatorViewBaseInfoMonitorContentId();
            String name1 = createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs.getName();
            Integer seq1 = createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs.getSeq();
            if (StringUtils.isBlank(indicatorViewBaseInfoMonitorContentId)) {
              indicatorViewBaseInfoMonitorContentId = idGenerator.nextIdStr();
              indicatorViewBaseInfoMonitorContentEntity = IndicatorViewBaseInfoMonitorContentEntity
                  .builder()
                  .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentId)
                  .appId(appId)
                  .indicatorViewBaseInfoMonitorId(finalIndicatorViewBaseInfoMonitorId)
                  .name(name1)
                  .seq(seq1)
                  .build();
            } else {
              indicatorViewBaseInfoMonitorContentEntity = kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentEntityMap.get(indicatorViewBaseInfoMonitorContentId);
              indicatorViewBaseInfoMonitorContentEntity.setName(name1);
              indicatorViewBaseInfoMonitorContentEntity.setSeq(seq1);
            }
            indicatorViewBaseInfoMonitorContentEntityList.add(indicatorViewBaseInfoMonitorContentEntity);
            String finalIndicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentId;
            createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs.getCreateOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRsList()
                .forEach(createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs -> {
                  IndicatorViewBaseInfoMonitorContentRefEntity indicatorViewBaseInfoMonitorContentRefEntity = null;
                  String indicatorViewBaseInfoMonitorContentRefId = createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs.getIndicatorViewBaseInfoMonitorContentRefId();
                  String indicatorInstanceId = createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs.getIndicatorInstanceId();
                  Integer seq2 = createOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs.getSeq();
                  if (StringUtils.isBlank(indicatorViewBaseInfoMonitorContentRefId)) {
                    indicatorViewBaseInfoMonitorContentRefId = idGenerator.nextIdStr();
                    indicatorViewBaseInfoMonitorContentRefEntity = IndicatorViewBaseInfoMonitorContentRefEntity
                        .builder()
                        .indicatorViewBaseInfoMonitorContentRefId(indicatorViewBaseInfoMonitorContentRefId)
                        .appId(appId)
                        .indicatorViewBaseInfoMonitorContentId(finalIndicatorViewBaseInfoMonitorContentId)
                        .indicatorInstanceId(indicatorInstanceId)
                        .seq(seq2)
                        .build();
                  } else {
                    indicatorViewBaseInfoMonitorContentRefEntity = kIndicatorViewBaseInfoMonitorContentRefIdVIndicatorViewBaseInfoMonitorContentRefEntityMap.get(indicatorViewBaseInfoMonitorContentRefId);
                    indicatorViewBaseInfoMonitorContentRefEntity.setIndicatorInstanceId(indicatorInstanceId);
                    indicatorViewBaseInfoMonitorContentRefEntity.setSeq(seq2);
                  }
                  indicatorViewBaseInfoMonitorContentRefEntityList.add(indicatorViewBaseInfoMonitorContentRefEntity);
                });
          });
    });
  }

  private void populateIndicatorViewBaseInfoSingleRelatedList(
      List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList, Set<String> paramIndicatorInstanceIdSet,
      String appId, List<CreateOrUpdateIndicatorViewBaseInfoSingleRs> createOrUpdateIndicatorViewBaseInfoSingleRsList,
      String indicatorViewBaseInfoId) {
    Set<String> paramIndicatorViewBaseInfoSingleIdSet = new HashSet<>();
    Set<String> dbIndicatorViewBaseInfoSingleIdSet = new HashSet<>();
    Map<String, IndicatorViewBaseInfoSingleEntity> kIndicatorViewBaseInfoSingleIdVIndicatorViewBaseInfoSingleEntityMap = new HashMap<>();
    createOrUpdateIndicatorViewBaseInfoSingleRsList.forEach(
        createOrUpdateIndicatorViewBaseInfoSingleRs -> {
          String indicatorInstanceId = createOrUpdateIndicatorViewBaseInfoSingleRs.getIndicatorInstanceId();
          paramIndicatorInstanceIdSet.add(indicatorInstanceId);
          String indicatorViewBaseInfoSingleId = createOrUpdateIndicatorViewBaseInfoSingleRs.getIndicatorViewBaseInfoSingleId();
          if (StringUtils.isNotBlank(indicatorViewBaseInfoSingleId)) {
            paramIndicatorViewBaseInfoSingleIdSet.add(indicatorViewBaseInfoSingleId);
          }
        }
    );
    if (!paramIndicatorViewBaseInfoSingleIdSet.isEmpty()) {
      indicatorViewBaseInfoSingleService.lambdaQuery()
          .eq(IndicatorViewBaseInfoSingleEntity::getAppId, appId)
          .in(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoSingleId, paramIndicatorViewBaseInfoSingleIdSet)
          .list()
          .forEach(indicatorViewBaseInfoSingleEntity -> {
            String indicatorViewBaseInfoSingleId = indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoSingleId();
            dbIndicatorViewBaseInfoSingleIdSet.add(indicatorViewBaseInfoSingleId);
            kIndicatorViewBaseInfoSingleIdVIndicatorViewBaseInfoSingleEntityMap.put(indicatorViewBaseInfoSingleId, indicatorViewBaseInfoSingleEntity);
          });
      if (
          paramIndicatorViewBaseInfoSingleIdSet.stream().anyMatch(indicatorViewBaseInfoSingleId -> !dbIndicatorViewBaseInfoSingleIdSet.contains(indicatorViewBaseInfoSingleId))
      ) {
        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field createOrUpdateIndicatorViewBaseInfoSingleRsList indicatorViewBaseInfoSingleId:{} is illegal", paramIndicatorViewBaseInfoSingleIdSet);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
    }
    createOrUpdateIndicatorViewBaseInfoSingleRsList.forEach(
        createOrUpdateIndicatorViewBaseInfoSingleRs -> {
          IndicatorViewBaseInfoSingleEntity indicatorViewBaseInfoSingleEntity = null;
          String indicatorViewBaseInfoSingleId = createOrUpdateIndicatorViewBaseInfoSingleRs.getIndicatorViewBaseInfoSingleId();
          String indicatorInstanceId = createOrUpdateIndicatorViewBaseInfoSingleRs.getIndicatorInstanceId();
          Integer seq = createOrUpdateIndicatorViewBaseInfoSingleRs.getSeq();
          if (StringUtils.isBlank(indicatorViewBaseInfoSingleId)) {
            indicatorViewBaseInfoSingleId = idGenerator.nextIdStr();
            indicatorViewBaseInfoSingleEntity = IndicatorViewBaseInfoSingleEntity
                .builder()
                .indicatorViewBaseInfoSingleId(indicatorViewBaseInfoSingleId)
                .appId(appId)
                .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
                .indicatorInstanceId(indicatorInstanceId)
                .seq(seq)
                .build();
          } else {
            indicatorViewBaseInfoSingleEntity = kIndicatorViewBaseInfoSingleIdVIndicatorViewBaseInfoSingleEntityMap.get(indicatorViewBaseInfoSingleId);
            indicatorViewBaseInfoSingleEntity.setIndicatorInstanceId(indicatorInstanceId);
            indicatorViewBaseInfoSingleEntity.setSeq(seq);
          }
          indicatorViewBaseInfoSingleEntityList.add(indicatorViewBaseInfoSingleEntity);
        }
    );
  }

  public void createOrUpdateRs(CreateOrUpdateIndicatorViewBaseInfoRequestRs createOrUpdateIndicatorViewBaseInfoRequestRs) throws InterruptedException {
      String appId = createOrUpdateIndicatorViewBaseInfoRequestRs.getAppId();
      String indicatorFuncId = createOrUpdateIndicatorViewBaseInfoRequestRs.getIndicatorFuncId();
      IndicatorViewBaseInfoEntity indicatorViewBaseInfoEntity = null;
      List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = new ArrayList<>();
      List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = new ArrayList<>();
      List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList = new ArrayList<>();
      List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = new ArrayList<>();
      List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = new ArrayList<>();
      List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = new ArrayList<>();
      String indicatorViewBaseInfoId = createOrUpdateIndicatorViewBaseInfoRequestRs.getIndicatorViewBaseInfoId();
      if (StringUtils.isBlank(indicatorViewBaseInfoId)) {
        indicatorViewBaseInfoId = idGenerator.nextIdStr();
        indicatorViewBaseInfoEntity = IndicatorViewBaseInfoEntity
            .builder()
            .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
            .appId(appId)
            .indicatorFuncId(indicatorFuncId)
            .build();
      } else {
        indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getAppId, appId)
            .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
            .oneOpt()
            .orElseThrow(() -> {
              log.warn("method createIndicatorViewBaseInfoRs param createIndicatorViewBaseInfoRs field indicatorFuncId:{} is illegal", indicatorFuncId);
              throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
            });
        String finalIndicatorViewBaseInfoId1 = indicatorViewBaseInfoId;
        indicatorViewBaseInfoEntity = indicatorViewBaseInfoService.lambdaQuery()
            .eq(IndicatorViewBaseInfoEntity::getAppId, appId)
            .eq(IndicatorViewBaseInfoEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
            .oneOpt()
            .orElseThrow(() -> {
              log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field indicatorViewBaseInfoId:{} is illegal", finalIndicatorViewBaseInfoId1);
              throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
            });
        Set<String> paramIndicatorInstanceIdSet = new HashSet<>();
        Set<String> dbIndicatorInstanceIdSet = new HashSet<>();
        List<CreateOrUpdateIndicatorViewBaseInfoDescrRs> createOrUpdateIndicatorViewBaseInfoDescrRsList = createOrUpdateIndicatorViewBaseInfoRequestRs.getCreateOrUpdateIndicatorViewBaseInfoDescrRsList();
        if (Objects.nonNull(createOrUpdateIndicatorViewBaseInfoDescrRsList) && !createOrUpdateIndicatorViewBaseInfoDescrRsList.isEmpty()) {
          populateIndicatorViewBaseInfoDescrRelatedList(
              indicatorViewBaseInfoDescrEntityList, indicatorViewBaseInfoDescrRefEntityList,
              paramIndicatorInstanceIdSet, appId, createOrUpdateIndicatorViewBaseInfoDescrRsList, indicatorViewBaseInfoId);
        }
        List<CreateOrUpdateIndicatorViewBaseInfoMonitorRs> createOrUpdateIndicatorViewBaseInfoMonitorRsList = createOrUpdateIndicatorViewBaseInfoRequestRs.getCreateOrUpdateIndicatorViewBaseInfoMonitorRsList();
        if (Objects.nonNull(createOrUpdateIndicatorViewBaseInfoMonitorRsList) && !createOrUpdateIndicatorViewBaseInfoMonitorRsList.isEmpty()) {
          populateIndicatorViewBaseInfoMonitorRelatedList(
              indicatorViewBaseInfoMonitorEntityList, indicatorViewBaseInfoMonitorContentEntityList, indicatorViewBaseInfoMonitorContentRefEntityList,
              paramIndicatorInstanceIdSet, appId, createOrUpdateIndicatorViewBaseInfoMonitorRsList, indicatorViewBaseInfoId
          );
        }
        List<CreateOrUpdateIndicatorViewBaseInfoSingleRs> createOrUpdateIndicatorViewBaseInfoSingleRsList = createOrUpdateIndicatorViewBaseInfoRequestRs.getCreateOrUpdateIndicatorViewBaseInfoSingleRsList();
        if (Objects.nonNull(createOrUpdateIndicatorViewBaseInfoSingleRsList) && !createOrUpdateIndicatorViewBaseInfoSingleRsList.isEmpty()) {
          populateIndicatorViewBaseInfoSingleRelatedList(
              indicatorViewBaseInfoSingleEntityList, paramIndicatorInstanceIdSet, appId, createOrUpdateIndicatorViewBaseInfoSingleRsList, indicatorViewBaseInfoId
          );
        }
        if (!paramIndicatorInstanceIdSet.isEmpty()) {
          indicatorInstanceService.lambdaQuery()
              .eq(IndicatorInstanceEntity::getAppId, appId)
              .in(IndicatorInstanceEntity::getIndicatorInstanceId, paramIndicatorInstanceIdSet)
              .list()
              .forEach(indicatorInstanceEntity -> dbIndicatorInstanceIdSet.add(indicatorInstanceEntity.getIndicatorInstanceId()));
          if (
              paramIndicatorInstanceIdSet.stream().anyMatch(indicatorInstanceId -> !dbIndicatorInstanceIdSet.contains(indicatorInstanceId))
          ) {
            log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field indicatorInstanceId:{} is illegal", paramIndicatorInstanceIdSet);
            throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
          }
        }
      }
      RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
      boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
      if (!isLocked) {
        throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
      }
      try {
        indicatorViewBaseInfoService.saveOrUpdate(indicatorViewBaseInfoEntity);
        indicatorViewBaseInfoDescrService.saveOrUpdateBatch(indicatorViewBaseInfoDescrEntityList);
        indicatorViewBaseInfoDescrRefService.saveOrUpdateBatch(indicatorViewBaseInfoDescrRefEntityList);
        indicatorViewBaseInfoMonitorService.saveOrUpdateBatch(indicatorViewBaseInfoMonitorEntityList);
        indicatorViewBaseInfoMonitorContentService.saveOrUpdateBatch(indicatorViewBaseInfoMonitorContentEntityList);
        indicatorViewBaseInfoMonitorContentRefService.saveOrUpdateBatch(indicatorViewBaseInfoMonitorContentRefEntityList);
        indicatorViewBaseInfoSingleService.saveOrUpdateBatch(indicatorViewBaseInfoSingleEntityList);
      } finally {
        lock.unlock();
      }
    }

  public IndicatorViewBaseInfoResponseRs getRs(String indicatorViewBaseInfoId) {
      IndicatorViewBaseInfoEntity indicatorViewBaseInfoEntity = indicatorViewBaseInfoService.lambdaQuery()
          .eq(IndicatorViewBaseInfoEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
          .one();
      return indicatorViewBaseInfo2ResponseRs(indicatorViewBaseInfoEntity);
  }

    public IndicatorViewBaseInfoResponseRs getRsByIndicatorFuncId(String indicatorFuncId) {
      if (StringUtils.isBlank(indicatorFuncId)) {
        log.warn("method IndicatorViewBaseInfoResponseRs param indicatorFuncId:{} is illegal", indicatorFuncId);
        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
      }
      IndicatorViewBaseInfoEntity indicatorViewBaseInfoEntity = indicatorViewBaseInfoService.lambdaQuery()
          .eq(IndicatorViewBaseInfoEntity::getIndicatorFuncId, indicatorFuncId)
          .one();
      return indicatorViewBaseInfo2ResponseRs(indicatorViewBaseInfoEntity);
    }

    /**
    * @param
    * @return
    * @说明: 删除指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorViewBaseInfo(String indicatorViewBaseInfoId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 批量删除
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void batchDelete(String string ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewBaseInfo(UpdateIndicatorViewBaseInfoRequest updateIndicatorViewBaseInfo ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewBaseInfoResponse getIndicatorViewBaseInfo(String indicatorViewBaseInfoId ) {
        return new IndicatorViewBaseInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewBaseInfoResponse> listIndicatorViewBaseInfo(String appId, String indicatorCategoryId, String name ) {
        return new ArrayList<IndicatorViewBaseInfoResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewBaseInfo(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name ) {
        return new String();
    }
}