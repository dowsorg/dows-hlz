package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorInstanceException;
import org.dows.hep.api.exception.IndicatorViewMonitorFollowupException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @description project descr:指标:查看指标监测随访类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class IndicatorViewMonitorFollowupBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-view-monitor-followup-create-delete-update:5000}")
    private Integer leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate;
    private final String indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId = "indicatorViewMonitorFollowupId";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorViewMonitorFollowupService indicatorViewMonitorFollowupService;
    private final IndicatorViewMonitorFollowupFollowupContentService indicatorViewMonitorFollowupFollowupContentService;
    private final IndicatorViewMonitorFollowupContentRefService indicatorViewMonitorFollowupContentRefService;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorInstanceService indicatorInstanceService;
    private final IndicatorFuncService indicatorFuncService;

    public static IndicatorViewMonitorFollowupResponseRs indicatorViewMonitorFollowup2ResponseRs(
        IndicatorViewMonitorFollowupEntity indicatorViewMonitorFollowupEntity,
        IndicatorCategoryResponse indicatorCategoryResponse,
        List<IndicatorViewMonitorFollowupFollowupContentResponseRs> indicatorViewMonitorFollowupFollowupContentResponseRsList
    ) {
        return IndicatorViewMonitorFollowupResponseRs
            .builder()
            .id(indicatorViewMonitorFollowupEntity.getId())
            .indicatorViewMonitorFollowupId(indicatorViewMonitorFollowupEntity.getIndicatorViewMonitorFollowupId())
            .appId(indicatorViewMonitorFollowupEntity.getAppId())
            .indicatorFuncId(indicatorViewMonitorFollowupEntity.getIndicatorFuncId())
            .name(indicatorViewMonitorFollowupEntity.getName())
            .status(indicatorViewMonitorFollowupEntity.getStatus())
            .dt(indicatorViewMonitorFollowupEntity.getDt())
            .indicatorCategoryResponse(indicatorCategoryResponse)
            .indicatorViewMonitorFollowupFollowupContentResponseRsList(indicatorViewMonitorFollowupFollowupContentResponseRsList)
            .build();
    }

    private List<IndicatorViewMonitorFollowupResponseRs> indicatorViewMonitorFollowupEntityList2ResponseRsList(
        List<IndicatorViewMonitorFollowupEntity> indicatorViewMonitorFollowupEntityList
    ) {
        if (Objects.isNull(indicatorViewMonitorFollowupEntityList) || indicatorViewMonitorFollowupEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorViewMonitorFollowupEntityList.get(0).getAppId();
        Set<String> indicatorViewMonitorFollowupIdSet = new HashSet<>();
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        indicatorViewMonitorFollowupEntityList.forEach(
            indicatorViewMonitorFollowupEntity -> {
                indicatorViewMonitorFollowupIdSet.add(indicatorViewMonitorFollowupEntity.getIndicatorViewMonitorFollowupId());
                indicatorCategoryIdSet.add(indicatorViewMonitorFollowupEntity.getIndicatorCategoryId());
            });
        Map<String, IndicatorCategoryResponse> kIndicatorCategoryIdVIndicatorCategoryResponseMap = new HashMap<>();
        if (!indicatorCategoryIdSet.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
                .list()
                .stream()
                .map(IndicatorCategoryBiz::indicatorCategoryEntity2Response).filter(Objects::nonNull)
                .forEach(indicatorCategoryResponse -> kIndicatorCategoryIdVIndicatorCategoryResponseMap.put(
                    indicatorCategoryResponse.getIndicatorCategoryId(), indicatorCategoryResponse
                ));
        }
        Set<String> indicatorViewMonitorFollowupFollowupContentIdSet = new HashSet<>();
        Map<String, List<IndicatorViewMonitorFollowupFollowupContentEntity>> kIndicatorViewMonitorFollowupIdVIndicatorViewMonitorFollowupFollowupContentListMap = new HashMap<>();
        if (!indicatorViewMonitorFollowupIdSet.isEmpty()) {
            indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
                .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
                .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupIdSet)
                .list()
                .forEach(indicatorViewMonitorFollowupFollowupContentEntity -> {
                    String indicatorViewMonitorFollowupFollowupContentId = indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupFollowupContentId();
                    indicatorViewMonitorFollowupFollowupContentIdSet.add(indicatorViewMonitorFollowupFollowupContentId);
                    String indicatorViewMonitorFollowupId = indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupId();
                    List<IndicatorViewMonitorFollowupFollowupContentEntity> indicatorViewMonitorFollowupFollowupContentEntityList = kIndicatorViewMonitorFollowupIdVIndicatorViewMonitorFollowupFollowupContentListMap.get(indicatorViewMonitorFollowupId);
                    if (Objects.isNull(indicatorViewMonitorFollowupFollowupContentEntityList)) {
                        indicatorViewMonitorFollowupFollowupContentEntityList = new ArrayList<>();
                    }
                    indicatorViewMonitorFollowupFollowupContentEntityList.add(indicatorViewMonitorFollowupFollowupContentEntity);
                    kIndicatorViewMonitorFollowupIdVIndicatorViewMonitorFollowupFollowupContentListMap.put(indicatorViewMonitorFollowupId, indicatorViewMonitorFollowupFollowupContentEntityList);
                });
        }
        Map<String, List<IndicatorViewMonitorFollowupContentRefEntity>> kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupContentRefListMap = new HashMap<>();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceEntityMap = new HashMap<>();
        if (!indicatorViewMonitorFollowupFollowupContentIdSet.isEmpty()) {
            indicatorViewMonitorFollowupContentRefService.lambdaQuery()
                .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
                .in(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentIdSet)
                .list()
                .forEach(indicatorViewMonitorFollowupContentRefEntity -> {
                    String indicatorInstanceId = indicatorViewMonitorFollowupContentRefEntity.getIndicatorInstanceId();
                    indicatorInstanceIdSet.add(indicatorInstanceId);
                    String indicatorViewMonitorFollowupFollowupContentId = indicatorViewMonitorFollowupContentRefEntity.getIndicatorViewMonitorFollowupFollowupContentId();
                    List<IndicatorViewMonitorFollowupContentRefEntity> indicatorViewMonitorFollowupContentRefEntityList = kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupContentRefListMap.get(indicatorViewMonitorFollowupFollowupContentId);
                    if (Objects.isNull(indicatorViewMonitorFollowupContentRefEntityList)) {
                        indicatorViewMonitorFollowupContentRefEntityList = new ArrayList<>();
                    }
                    indicatorViewMonitorFollowupContentRefEntityList.add(indicatorViewMonitorFollowupContentRefEntity);
                    kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupContentRefListMap.put(
                        indicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupContentRefEntityList
                    );
                });
        }
        if (!indicatorInstanceIdSet.isEmpty()) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                .list()
                .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceEntityMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity));
        }
        return indicatorViewMonitorFollowupEntityList
            .stream()
            .map(indicatorViewMonitorFollowupEntity -> {
              List<IndicatorViewMonitorFollowupFollowupContentEntity> indicatorViewMonitorFollowupEntityList1 = kIndicatorViewMonitorFollowupIdVIndicatorViewMonitorFollowupFollowupContentListMap.get(indicatorViewMonitorFollowupEntity.getIndicatorViewMonitorFollowupId());
              if (Objects.isNull(indicatorViewMonitorFollowupEntityList1)) {
                indicatorViewMonitorFollowupEntityList1 = new ArrayList<>();
              }
              return IndicatorViewMonitorFollowupBiz.indicatorViewMonitorFollowup2ResponseRs(
                  indicatorViewMonitorFollowupEntity,
                  kIndicatorCategoryIdVIndicatorCategoryResponseMap.get(indicatorViewMonitorFollowupEntity.getIndicatorCategoryId()),
                  indicatorViewMonitorFollowupEntityList1
                      .stream()
                      .map(indicatorViewMonitorFollowupFollowupContentEntity -> {
                        List<IndicatorViewMonitorFollowupContentRefEntity> indicatorViewMonitorFollowupContentRefEntityList = kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupContentRefListMap.get(indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupFollowupContentId());
                        List<IndicatorViewMonitorFollowupContentRefResponseRs> indicatorViewMonitorFollowupContentRefResponseRsList = new ArrayList<>();
                        if (Objects.nonNull(indicatorViewMonitorFollowupContentRefEntityList)) {
                          indicatorViewMonitorFollowupContentRefResponseRsList = indicatorViewMonitorFollowupContentRefEntityList
                              .stream()
                              .map(indicatorViewMonitorFollowupContentRefEntity -> {
                                IndicatorInstanceResponseRs indicatorInstanceResponseRs = IndicatorInstanceBiz.indicatorInstance2ResponseRs(
                                    kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorViewMonitorFollowupContentRefEntity.getIndicatorInstanceId()),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                                );
                                return IndicatorViewMonitorFollowupContentRefBiz.indicatorViewMonitorFollowupContentRef2ResponseRs(
                                    indicatorViewMonitorFollowupContentRefEntity, indicatorInstanceResponseRs
                                );
                              })
                              .collect(Collectors.toList());
                        }
                        return IndicatorViewMonitorFollowupFollowupContentBiz.indicatorViewMonitorFollowupFollowupContent2ResponseRs(
                            indicatorViewMonitorFollowupFollowupContentEntity,
                            indicatorViewMonitorFollowupContentRefResponseRsList
                        );
                      })
                      .collect(Collectors.toList())
              );
            })
            .collect(Collectors.toList());
    }

    /**
    * @param
    * @return
    * @说明: 创建查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorViewMonitorFollowup(CreateIndicatorViewMonitorFollowupRequest createIndicatorViewMonitorFollowup ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void deleteIndicatorViewMonitorFollowup(String indicatorViewMonitorFollowupId) throws InterruptedException {
        IndicatorViewMonitorFollowupEntity indicatorViewMonitorFollowupEntity = indicatorViewMonitorFollowupService.lambdaQuery()
            .eq(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method deleteIndicatorViewMonitorFollowup param indicatorViewMonitorFollowupId:{} is illegal", indicatorViewMonitorFollowupId);
                throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
            });
        String appId = indicatorViewMonitorFollowupEntity.getAppId();
        Set<String> indicatorViewMonitorFollowupFollowupContentIdSet = indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
            .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
            .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
            .list()
            .stream()
            .map(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId)
            .collect(Collectors.toSet());
        boolean isRemoved = indicatorViewMonitorFollowupService.remove(
            new LambdaQueryWrapper<IndicatorViewMonitorFollowupEntity>()
                .eq(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
        );
        if (!isRemoved) {
            log.warn("method deleteIndicatorViewMonitorFollowup param indicatorViewMonitorFollowupId:{} is illegal", indicatorViewMonitorFollowupId);
            throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
        }
        if (!indicatorViewMonitorFollowupFollowupContentIdSet.isEmpty()) {
            indicatorViewMonitorFollowupFollowupContentService.remove(
                new LambdaQueryWrapper<IndicatorViewMonitorFollowupFollowupContentEntity>()
                    .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
                    .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentIdSet)
            );
            indicatorViewMonitorFollowupContentRefService.remove(
                new LambdaQueryWrapper<IndicatorViewMonitorFollowupContentRefEntity>()
                    .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
                    .in(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentIdSet)
            );
        }
    }

  @Transactional(rollbackFor = Exception.class)
  public void createOrUpdateRs(CreateOrUpdateIndicatorViewMonitorFollowupRequestRs createOrUpdateIndicatorViewMonitorFollowupRequestRs) throws InterruptedException {
    IndicatorViewMonitorFollowupEntity indicatorViewMonitorFollowupEntity = null;
    List<IndicatorViewMonitorFollowupFollowupContentEntity> indicatorViewMonitorFollowupFollowupContentEntityList = new ArrayList<>();
    List<IndicatorViewMonitorFollowupContentRefEntity> indicatorViewMonitorFollowupContentRefEntityList = new ArrayList<>();
    String appId = createOrUpdateIndicatorViewMonitorFollowupRequestRs.getAppId();
    String indicatorFuncId = createOrUpdateIndicatorViewMonitorFollowupRequestRs.getIndicatorFuncId();
    if (StringUtils.isNotBlank(indicatorFuncId)) {
      indicatorFuncService.lambdaQuery()
          .eq(IndicatorFuncEntity::getAppId, appId)
          .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
          .oneOpt()
          .orElseThrow(() -> {
            log.warn("method IndicatorViewMonitorFollowupBiz.createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
            throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
          });
    }
    String indicatorCategoryId = createOrUpdateIndicatorViewMonitorFollowupRequestRs.getIndicatorCategoryId();
    if (StringUtils.isNotBlank(indicatorCategoryId)) {
      indicatorCategoryService.lambdaQuery()
          .eq(IndicatorCategoryEntity::getAppId, appId)
          .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
          .oneOpt()
          .orElseThrow(() -> {
            log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
            throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
          });
    }
    String indicatorViewMonitorFollowupId = createOrUpdateIndicatorViewMonitorFollowupRequestRs.getIndicatorViewMonitorFollowupId();
    if (StringUtils.isBlank(indicatorViewMonitorFollowupId)) {
      indicatorViewMonitorFollowupId = idGenerator.nextIdStr();
      indicatorViewMonitorFollowupEntity = IndicatorViewMonitorFollowupEntity
          .builder()
          .indicatorViewMonitorFollowupId(indicatorViewMonitorFollowupId)
          .appId(appId)
          .indicatorFuncId(indicatorFuncId)
          .name(createOrUpdateIndicatorViewMonitorFollowupRequestRs.getName())
          .indicatorCategoryId(indicatorCategoryId)
          .status(createOrUpdateIndicatorViewMonitorFollowupRequestRs.getStatus())
          .build();
      indicatorViewMonitorFollowupEntity.setIndicatorViewMonitorFollowupId(indicatorViewMonitorFollowupId);
    } else {
      indicatorViewMonitorFollowupEntity = indicatorViewMonitorFollowupService.lambdaQuery()
          .eq(IndicatorViewMonitorFollowupEntity::getAppId, appId)
          .eq(IndicatorViewMonitorFollowupEntity::getIndicatorFuncId, indicatorFuncId)
          .eq(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
          .oneOpt()
          .orElseThrow(() -> {
            log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs:{} is illegal", createOrUpdateIndicatorViewMonitorFollowupRequestRs);
            throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
          });
      indicatorViewMonitorFollowupEntity.setName(createOrUpdateIndicatorViewMonitorFollowupRequestRs.getName());
      indicatorViewMonitorFollowupEntity.setStatus(createOrUpdateIndicatorViewMonitorFollowupRequestRs.getStatus());
      indicatorViewMonitorFollowupEntity.setIndicatorCategoryId(createOrUpdateIndicatorViewMonitorFollowupRequestRs.getIndicatorCategoryId());
    }
    List<CreateOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs> createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList = createOrUpdateIndicatorViewMonitorFollowupRequestRs.getCreateOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList();
    if (!createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList.isEmpty()) {
      Set<String> paramIndicatorViewMonitorFollowupFollowupContentIdSet = new HashSet<>();
      Set<String> dbIndicatorViewMonitorFollowupFollowupContentIdSet = new HashSet<>();
      Map<String, IndicatorViewMonitorFollowupFollowupContentEntity> kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupFollowupContentMap = new HashMap<>();
      Set<String> paramIndicatorViewMonitorFollowupContentRefIdSet = new HashSet<>();
      Set<String> dbIndicatorViewMonitorFollowupContentRefIdSet = new HashSet<>();
      Map<String, IndicatorViewMonitorFollowupContentRefEntity> kIndicatorViewMonitorFollowupContentRefIdVIndicatorViewMonitorFollowupContentRefMap = new HashMap<>();
      Set<String> paramIndicatorInstanceIdSet = new HashSet<>();
      Set<String> dbIndicatorInstanceIdSet = new HashSet<>();
      createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList.forEach(createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs -> {
            String indicatorViewMonitorFollowupFollowupContentId = createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs.getIndicatorViewMonitorFollowupFollowupContentId();
            if (StringUtils.isNotBlank(indicatorViewMonitorFollowupFollowupContentId)) {
              paramIndicatorViewMonitorFollowupFollowupContentIdSet.add(indicatorViewMonitorFollowupFollowupContentId);
            }
            createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs.getCreateOrUpdateIndicatorViewMonitorFollowupContentRefRequestRsList()
                .forEach(createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs -> {
                  String indicatorViewMonitorFollowupContentRefId = createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getIndicatorViewMonitorFollowupContentRefId();
                  if (StringUtils.isNotBlank(indicatorViewMonitorFollowupContentRefId)) {
                    paramIndicatorViewMonitorFollowupContentRefIdSet.add(indicatorViewMonitorFollowupContentRefId);
                  }
                  String indicatorInstanceId = createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getIndicatorInstanceId();
                  if (StringUtils.isNotBlank(indicatorInstanceId)) {
                    paramIndicatorInstanceIdSet.add(indicatorInstanceId);
                  } else {
                    log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs indicatorInstanceId is blank");
                    throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
                  }
                });
          });
      if (!paramIndicatorViewMonitorFollowupFollowupContentIdSet.isEmpty()) {
        indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
            .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
            .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, paramIndicatorViewMonitorFollowupFollowupContentIdSet)
            .list()
            .forEach(indicatorViewMonitorFollowupFollowupContentEntity -> {
              dbIndicatorViewMonitorFollowupFollowupContentIdSet.add(indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupFollowupContentId());
              kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupFollowupContentMap.put(
                  indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupFollowupContentId(), indicatorViewMonitorFollowupFollowupContentEntity);
            });
        if (
            paramIndicatorViewMonitorFollowupFollowupContentIdSet.stream().anyMatch(indicatorViewMonitorFollowupFollowupContentId -> !dbIndicatorViewMonitorFollowupFollowupContentIdSet.contains(indicatorViewMonitorFollowupFollowupContentId))
        ) {
          log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs indicatorViewMonitorFollowupFollowupContentId is illegal");
          throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
        }
      }
      if (!paramIndicatorViewMonitorFollowupContentRefIdSet.isEmpty()) {
        indicatorViewMonitorFollowupContentRefService.lambdaQuery()
            .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
            .in(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupContentRefId, paramIndicatorViewMonitorFollowupContentRefIdSet)
            .list()
            .forEach(indicatorViewMonitorFollowupContentRefEntity -> {
              dbIndicatorViewMonitorFollowupContentRefIdSet.add(indicatorViewMonitorFollowupContentRefEntity.getIndicatorViewMonitorFollowupContentRefId());
              kIndicatorViewMonitorFollowupContentRefIdVIndicatorViewMonitorFollowupContentRefMap.put(
                  indicatorViewMonitorFollowupContentRefEntity.getIndicatorViewMonitorFollowupContentRefId(), indicatorViewMonitorFollowupContentRefEntity
              );
            });
        if (
            paramIndicatorViewMonitorFollowupContentRefIdSet.stream().anyMatch(indicatorViewMonitorFollowupContentRefId -> !dbIndicatorViewMonitorFollowupContentRefIdSet.contains(indicatorViewMonitorFollowupContentRefId))
        ) {
          log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs indicatorViewMonitorFollowupContentRefId is illegal");
          throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
        }
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
          log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewMonitorFollowupRequestRs indicatorInstanceId is illegal");
          throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
        }
      }
      String finalIndicatorViewMonitorFollowupId = indicatorViewMonitorFollowupId;
      createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRsList.forEach(createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs -> {
        String indicatorViewMonitorFollowupFollowupContentId = createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs.getIndicatorViewMonitorFollowupFollowupContentId();
        String name = createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs.getName();
        Integer seq = createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs.getSeq();
        IndicatorViewMonitorFollowupFollowupContentEntity indicatorViewMonitorFollowupFollowupContentEntity = null;
        if (StringUtils.isBlank(indicatorViewMonitorFollowupFollowupContentId)) {
          indicatorViewMonitorFollowupFollowupContentId = idGenerator.nextIdStr();
          indicatorViewMonitorFollowupFollowupContentEntity = IndicatorViewMonitorFollowupFollowupContentEntity
              .builder()
              .indicatorViewMonitorFollowupFollowupContentId(indicatorViewMonitorFollowupFollowupContentId)
              .appId(appId)
              .indicatorViewMonitorFollowupId(finalIndicatorViewMonitorFollowupId)
              .name(name)
              .seq(seq)
              .build();
        } else {
          indicatorViewMonitorFollowupFollowupContentEntity = kIndicatorViewMonitorFollowupFollowupContentIdVIndicatorViewMonitorFollowupFollowupContentMap.get(indicatorViewMonitorFollowupFollowupContentId);
          indicatorViewMonitorFollowupFollowupContentEntity.setName(name);
          indicatorViewMonitorFollowupFollowupContentEntity.setSeq(seq);
        }
        indicatorViewMonitorFollowupFollowupContentEntityList.add(indicatorViewMonitorFollowupFollowupContentEntity);
        String finalIndicatorViewMonitorFollowupFollowupContentId = indicatorViewMonitorFollowupFollowupContentId;
        createOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs.getCreateOrUpdateIndicatorViewMonitorFollowupContentRefRequestRsList()
            .forEach(createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs -> {
              String indicatorViewMonitorFollowupContentRefId = createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getIndicatorViewMonitorFollowupContentRefId();
              IndicatorViewMonitorFollowupContentRefEntity indicatorViewMonitorFollowupContentRefEntity = null;
              if (StringUtils.isBlank(indicatorViewMonitorFollowupContentRefId)) {
                indicatorViewMonitorFollowupContentRefEntity = IndicatorViewMonitorFollowupContentRefEntity
                    .builder()
                    .indicatorViewMonitorFollowupContentRefId(idGenerator.nextIdStr())
                    .appId(appId)
                    .indicatorViewMonitorFollowupFollowupContentId(finalIndicatorViewMonitorFollowupFollowupContentId)
                    .indicatorInstanceId(createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getIndicatorInstanceId())
                    .seq(createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getSeq())
                    .build();
              } else {
                indicatorViewMonitorFollowupContentRefEntity = kIndicatorViewMonitorFollowupContentRefIdVIndicatorViewMonitorFollowupContentRefMap.get(indicatorViewMonitorFollowupContentRefId);
                indicatorViewMonitorFollowupContentRefEntity.setIndicatorInstanceId(createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getIndicatorInstanceId());
                indicatorViewMonitorFollowupContentRefEntity.setSeq(createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs.getSeq());
              }
              indicatorViewMonitorFollowupContentRefEntityList.add(indicatorViewMonitorFollowupContentRefEntity);
            });
      });
    }
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_MONITOR_FOLLOWUP_CREATE_DELETE_UPDATE, indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_MONITOR_FOLLOWUP_LATER);
    }
    try {
      indicatorViewMonitorFollowupService.saveOrUpdate(indicatorViewMonitorFollowupEntity);
      indicatorViewMonitorFollowupFollowupContentService.saveOrUpdateBatch(indicatorViewMonitorFollowupFollowupContentEntityList);
      indicatorViewMonitorFollowupContentRefService.saveOrUpdateBatch(indicatorViewMonitorFollowupContentRefEntityList);
    } finally {
      lock.unlock();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void batchDeleteRs(List<String> indicatorViewMonitorFollowupIdList) {
    if (indicatorViewMonitorFollowupIdList.isEmpty()) {
      log.warn("method IndicatorViewMonitorFollowupBiz.batchDeleteRs param indicatorViewMonitorFollowupIdList is empty");
      throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
    }
    Set<String> dbIndicatorViewMonitorFollowupIdSet = indicatorViewMonitorFollowupService.lambdaQuery()
        .in(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupIdList)
        .list()
        .stream()
        .map(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId)
        .collect(Collectors.toSet());
    if (
        indicatorViewMonitorFollowupIdList.stream().anyMatch(indicatorViewMonitorFollowupId -> !dbIndicatorViewMonitorFollowupIdSet.contains(indicatorViewMonitorFollowupId))
    ) {
      log.warn("method IndicatorViewMonitorFollowupBiz.batchDeleteRs param indicatorViewMonitorFollowupIdList is empty");
      throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
    }
    Set<String> dbIndicatorViewMonitorFollowupFollowupContentIdSet = indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
        .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupId, dbIndicatorViewMonitorFollowupIdSet)
        .list()
        .stream()
        .map(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId)
        .collect(Collectors.toSet());
    boolean isRemoved = indicatorViewMonitorFollowupService.remove(
        new LambdaQueryWrapper<IndicatorViewMonitorFollowupEntity>()
            .in(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupIdList)
    );
    if (!isRemoved) {
      log.warn("method IndicatorViewMonitorFollowupBiz.batchDeleteRs param indicatorViewMonitorFollowupIdList:{} is illegal", indicatorViewMonitorFollowupIdList);
      throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
    }
    if (!dbIndicatorViewMonitorFollowupFollowupContentIdSet.isEmpty()) {
      indicatorViewMonitorFollowupFollowupContentService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupFollowupContentEntity>()
              .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, dbIndicatorViewMonitorFollowupFollowupContentIdSet)
      );
      indicatorViewMonitorFollowupContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupContentRefEntity>()
              .in(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupFollowupContentId, dbIndicatorViewMonitorFollowupFollowupContentIdSet)
      );
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateStatusRs(String indicatorViewMonitorFollowupId, Integer status) {
    IndicatorViewMonitorFollowupEntity indicatorViewMonitorFollowupEntity = indicatorViewMonitorFollowupService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method IndicatorViewMonitorFollowupBiz.updateStatusRs param indicatorViewMonitorFollowupId:{} is illegal", indicatorViewMonitorFollowupId);
          throw new IndicatorViewMonitorFollowupException(EnumESC.VALIDATE_EXCEPTION);
        });
    indicatorViewMonitorFollowupEntity.setStatus(status);
    indicatorViewMonitorFollowupService.updateById(indicatorViewMonitorFollowupEntity);
  }

  public IndicatorViewMonitorFollowupResponseRs getRs(String indicatorViewMonitorFollowupId) {
    IndicatorViewMonitorFollowupEntity indicatorViewMonitorFollowupEntity = indicatorViewMonitorFollowupService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupEntity::getIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
        .one();
    if (Objects.isNull(indicatorViewMonitorFollowupEntity)) {
      return null;
    }
    List<IndicatorViewMonitorFollowupEntity> indicatorViewMonitorFollowupEntityList = new ArrayList<>();
    indicatorViewMonitorFollowupEntityList.add(indicatorViewMonitorFollowupEntity);
    List<IndicatorViewMonitorFollowupResponseRs> indicatorViewMonitorFollowupResponseRs = indicatorViewMonitorFollowupEntityList2ResponseRsList(indicatorViewMonitorFollowupEntityList);
    if (indicatorViewMonitorFollowupResponseRs.isEmpty()) {
      return null;
    }
    return indicatorViewMonitorFollowupResponseRs.get(0);
  }

  public Page<IndicatorViewMonitorFollowupResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
    Page<IndicatorViewMonitorFollowupEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
    LambdaQueryWrapper<IndicatorViewMonitorFollowupEntity> indicatorViewMonitorFollowupEntityLQW = new LambdaQueryWrapper<>();
    indicatorViewMonitorFollowupEntityLQW
        .eq(StringUtils.isNotBlank(appId), IndicatorViewMonitorFollowupEntity::getAppId, StringUtils.isBlank(appId) ? null : appId.trim())
        .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorViewMonitorFollowupEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(Objects.nonNull(status), IndicatorViewMonitorFollowupEntity::getStatus, status)
        .like(StringUtils.isNotBlank(name), IndicatorViewMonitorFollowupEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
    if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
      List<String> paramIndicatorCategoryIdList = Arrays.stream(indicatorCategoryIdList.split(",")).toList();
      indicatorViewMonitorFollowupEntityLQW.in(IndicatorViewMonitorFollowupEntity::getIndicatorCategoryId, paramIndicatorCategoryIdList);
    }
    Page<IndicatorViewMonitorFollowupEntity> indicatorViewMonitorFollowupEntityPage = indicatorViewMonitorFollowupService.page(page, indicatorViewMonitorFollowupEntityLQW);
    Page<IndicatorViewMonitorFollowupResponseRs> indicatorViewMonitorFollowupResponseRsPage = RsPageUtil.convertFromAnother(indicatorViewMonitorFollowupEntityPage);
    List<IndicatorViewMonitorFollowupEntity> indicatorViewMonitorFollowupEntityList = indicatorViewMonitorFollowupEntityPage.getRecords();
    List<IndicatorViewMonitorFollowupResponseRs> indicatorViewMonitorFollowupResponseRsList = indicatorViewMonitorFollowupEntityList2ResponseRsList(indicatorViewMonitorFollowupEntityList);
    indicatorViewMonitorFollowupResponseRsPage.setRecords(indicatorViewMonitorFollowupResponseRsList);
    return indicatorViewMonitorFollowupResponseRsPage;
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
    * @说明: 更改启用状态
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateStatus(IndicatorViewMonitorFollowupRequest indicatorViewMonitorFollowup ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewMonitorFollowup(UpdateIndicatorViewMonitorFollowupRequest updateIndicatorViewMonitorFollowup ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewMonitorFollowupResponse getIndicatorViewMonitorFollowup(String indicatorViewMonitorFollowupId ) {
        return new IndicatorViewMonitorFollowupResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewMonitorFollowupResponse> listIndicatorViewMonitorFollowup(String appId, String indicatorCategoryId, String name, Integer type, Integer status ) {
        return new ArrayList<IndicatorViewMonitorFollowupResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选查看指标监测随访类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewMonitorFollowup(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, Integer type, Integer status ) {
        return new String();
    }
}