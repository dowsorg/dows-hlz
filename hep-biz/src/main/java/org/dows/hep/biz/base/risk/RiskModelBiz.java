package org.dows.hep.biz.base.risk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskDangerPointRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskDeathModelRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateRiskModelRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;
import org.dows.hep.api.base.risk.request.CreateRiskModelRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskModelRequest;
import org.dows.hep.api.base.risk.request.UpdateStatusRiskModelRequest;
import org.dows.hep.api.base.risk.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.RiskModelException;
import org.dows.hep.biz.base.indicator.IndicatorInstanceBiz;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
* @description project descr:风险:风险模型
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskModelBiz{
    private final IdGenerator idGenerator;
    private final RiskCategoryService riskCategoryService;
    private final IndicatorInstanceService indicatorInstanceService;
    private final RiskDangerPointService riskDangerPointService;
    private final RiskDeathModelService riskDeathModelService;
    private final RiskModelService riskModelService;

    public static Integer computeRiskModelRiskDeathProbability(List<RiskDeathModelEntity> riskDeathModelEntityList) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        riskDeathModelEntityList.forEach(riskDeathModelEntity -> {
            atomicInteger.getAndAdd(riskDeathModelEntity.getRiskDeathProbability());
        });
        return atomicInteger.get();
    }

    public static RiskModelResponseRs riskModel2ResponseRs(
        RiskModelEntity riskModelEntity,
        RiskCategoryResponseRs riskCategoryResponseRs,
        List<RiskDeathModelResponseRs> riskDeathModelResponseRsList
    ) {
        return RiskModelResponseRs
            .builder()
            .id(riskModelEntity.getId())
            .riskModelId(riskModelEntity.getRiskModelId())
            .appId(riskModelEntity.getAppId())
            .riskCategoryResponseRs(riskCategoryResponseRs)
            .name(riskModelEntity.getName())
            .riskDeathProbability(riskModelEntity.getRiskDeathProbability())
            .status(riskModelEntity.getStatus())
            .dt(riskModelEntity.getDt())
            .riskDeathModelResponseRsList(riskDeathModelResponseRsList)
            .build();
    }

    private List<RiskModelResponseRs> riskModelList2ResponseRsList(List<RiskModelEntity> riskModelEntityList) {
        if (Objects.isNull(riskModelEntityList) || riskModelEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = riskModelEntityList.get(0).getAppId();
        Set<String> riskModelIdSet = new HashSet<>();
        Set<String> riskCategoryIdSet = new HashSet<>();
        Set<String> riskDeathModelIdSet = new HashSet<>();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        Map<String, RiskCategoryEntity> kRiskCategoryIdVRiskCategoryMap = new HashMap<>();
        Map<String, List<RiskDeathModelEntity>> kRiskModelIdVRiskDeathModelListMap = new HashMap<>();
        Map<String, List<RiskDangerPointEntity>> kRiskDeathModelIdVRiskDangerPointEntityListMap = new HashMap<>();
        Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceMap = new HashMap<>();
        riskModelEntityList.forEach(riskModelEntity -> {
            riskModelIdSet.add(riskModelEntity.getRiskModelId());
            riskCategoryIdSet.add(riskModelEntity.getRiskCategoryId());
        });
        if (!riskModelIdSet.isEmpty()) {
            riskDeathModelService.lambdaQuery()
                .eq(RiskDeathModelEntity::getAppId, appId)
                .in(RiskDeathModelEntity::getRiskModelId, riskModelIdSet)
                .list()
                .forEach(riskDeathModelEntity -> {
                    riskDeathModelIdSet.add(riskDeathModelEntity.getRiskDeathModelId());
                    String riskModelId = riskDeathModelEntity.getRiskModelId();
                    List<RiskDeathModelEntity> riskDeathModelEntityList = kRiskModelIdVRiskDeathModelListMap.get(riskModelId);
                    if (Objects.isNull(riskDeathModelEntityList)) {
                        riskDeathModelEntityList = new ArrayList<>();
                    }
                    riskDeathModelEntityList.add(riskDeathModelEntity);
                    kRiskModelIdVRiskDeathModelListMap.put(riskModelId, riskDeathModelEntityList);
                });
            if (!riskDeathModelIdSet.isEmpty()) {
                riskDangerPointService.lambdaQuery()
                    .eq(RiskDangerPointEntity::getAppId, appId)
                    .in(RiskDangerPointEntity::getRiskDeathModelId, riskDeathModelIdSet)
                    .list()
                    .forEach(riskDangerPointEntity -> {
                        indicatorInstanceIdSet.add(riskDangerPointEntity.getIndicatorInstanceId());
                        String riskDeathModelId = riskDangerPointEntity.getRiskDeathModelId();
                        List<RiskDangerPointEntity> riskDangerPointEntityList = kRiskDeathModelIdVRiskDangerPointEntityListMap.get(riskDeathModelId);
                        if (Objects.isNull(riskDangerPointEntityList)) {
                            riskDangerPointEntityList = new ArrayList<>();
                        }
                        riskDangerPointEntityList.add(riskDangerPointEntity);
                        kRiskDeathModelIdVRiskDangerPointEntityListMap.put(riskDeathModelId, riskDangerPointEntityList);
                    });
            }
            if (!indicatorInstanceIdSet.isEmpty()) {
                indicatorInstanceService.lambdaQuery()
                    .eq(IndicatorInstanceEntity::getAppId, appId)
                    .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                    .list()
                    .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceMap.put(
                        indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity
                    ));
            }
        }
        if (!riskCategoryIdSet.isEmpty()) {
            riskCategoryService.lambdaQuery()
                .eq(RiskCategoryEntity::getAppId, appId)
                .in(RiskCategoryEntity::getRiskCategoryId, riskCategoryIdSet)
                .list()
                .forEach(riskCategoryEntity -> kRiskCategoryIdVRiskCategoryMap.put(riskCategoryEntity.getRiskCategoryId(), riskCategoryEntity));
        }
        return riskModelEntityList
            .stream()
            .map(riskModelEntity -> {
                String riskCategoryId = riskModelEntity.getRiskCategoryId();
                RiskCategoryResponseRs riskCategoryResponseRs = RiskCategoryBiz.riskCategory2ResponseRs(kRiskCategoryIdVRiskCategoryMap.get(riskCategoryId));
                String riskModelId = riskModelEntity.getRiskModelId();
                List<RiskDeathModelResponseRs> riskDeathModelResponseRsList = kRiskModelIdVRiskDeathModelListMap.get(riskModelId)
                    .stream()
                    .map(riskDeathModelEntity -> {
                        String riskDeathModelId = riskDeathModelEntity.getRiskDeathModelId();
                        List<RiskDangerPointResponseRs> riskDangerPointResponseRsList = kRiskDeathModelIdVRiskDangerPointEntityListMap.get(riskDeathModelId)
                            .stream()
                            .map(riskDangerPointEntity -> {
                                String indicatorInstanceId = riskDangerPointEntity.getIndicatorInstanceId();
                                IndicatorInstanceResponseRs indicatorInstanceResponseRs = IndicatorInstanceBiz.indicatorInstance2ResponseRs(
                                    kIndicatorInstanceIdVIndicatorInstanceMap.get(indicatorInstanceId),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                                    );
                                return RiskDangerPointBiz.riskDangerPoint2ResponseRs(riskDangerPointEntity, indicatorInstanceResponseRs);
                            })
                            .collect(Collectors.toList());
                        return RiskDeathModelBiz.riskDeathModel2ResponseRs(riskDeathModelEntity, riskDangerPointResponseRsList);
                    })
                    .collect(Collectors.toList());
                return riskModel2ResponseRs(
                    riskModelEntity,
                    riskCategoryResponseRs,
                    riskDeathModelResponseRsList
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateRiskModelRequestRs createOrUpdateRiskModelRequestRs) {
        List<RiskDangerPointEntity> finalRiskDangerPointEntityList = new ArrayList<>();
        List<RiskDeathModelEntity> finalRiskDeathModelEntityList = new ArrayList<>();
        RiskModelEntity finalRiskModelEntity = null;
        String riskModelId = createOrUpdateRiskModelRequestRs.getRiskModelId();
        AtomicReference<String> riskModelIdAtomicReference = new AtomicReference<>(riskModelId);
        String appId = createOrUpdateRiskModelRequestRs.getAppId();
        String riskCategoryId = createOrUpdateRiskModelRequestRs.getRiskCategoryId();
        String name = createOrUpdateRiskModelRequestRs.getName();
        Integer status = createOrUpdateRiskModelRequestRs.getStatus();
        List<CreateOrUpdateRiskDeathModelRequestRs> createOrUpdateRiskDeathModelRequestRsList = createOrUpdateRiskModelRequestRs.getCreateOrUpdateRiskDeathModelRequestRsList();
        riskCategoryService.lambdaQuery()
            .eq(RiskCategoryEntity::getAppId, appId)
            .eq(RiskCategoryEntity::getRiskCategoryId, riskCategoryId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method RiskModelBiz.createOrUpdateRs param createOrUpdateRiskModelRequestRs riskCategoryId:{} is illegal", riskCategoryId);
                throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
            });
        if (StringUtils.isBlank(riskModelId)) {
            riskModelId = idGenerator.nextIdStr();
            riskModelIdAtomicReference.set(riskModelId);
            finalRiskModelEntity = RiskModelEntity
                .builder()
                .riskModelId(riskModelId)
                .appId(appId)
                .riskCategoryId(riskCategoryId)
                .name(name)
                .status(status)
                .build();
        } else {
            finalRiskModelEntity = riskModelService.lambdaQuery()
                .eq(RiskModelEntity::getAppId, appId)
                .eq(RiskModelEntity::getRiskModelId, riskModelId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method RiskModelBiz.createOrUpdateRs param createOrUpdateRiskModelRequestRs riskModelId:{} is illegal", riskModelIdAtomicReference.get());
                    throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
                });
            finalRiskModelEntity.setRiskCategoryId(riskCategoryId);
            finalRiskModelEntity.setName(name);
            finalRiskModelEntity.setStatus(status);
        }
        Set<String> paramRiskDeathModelIdSet = new HashSet<>();
        Set<String> dbRiskDeathModelIdSet = new HashSet<>();
        Map<String, RiskDeathModelEntity> kRiskDeathModelIdVRiskDeathModelMap = new HashMap<>();
        Set<String> paramRiskDangerPointIdSet = new HashSet<>();
        Set<String> dbRiskDangerPointIdSet = new HashSet<>();
        Map<String, RiskDangerPointEntity> kRiskDangerPointIdVRiskDangerPointMap = new HashMap<>();
        Set<String> paramIndicatorInstanceIdSet = new HashSet<>();
        Set<String> dbIndicatorInstanceIdSet = new HashSet<>();
        createOrUpdateRiskDeathModelRequestRsList.forEach(createOrUpdateRiskDeathModelRequestRs -> {
            String riskDeathModelId = createOrUpdateRiskDeathModelRequestRs.getRiskDeathModelId();
            if (StringUtils.isNotBlank(riskDeathModelId)) {
                paramRiskDeathModelIdSet.add(riskDeathModelId);
            }
            List<CreateOrUpdateRiskDangerPointRequestRs> createOrUpdateRiskDangerPointRequestRsList = createOrUpdateRiskDeathModelRequestRs.getCreateOrUpdateRiskDangerPointRequestRsList();
            createOrUpdateRiskDangerPointRequestRsList.forEach(createOrUpdateRiskDangerPointRequestRs -> {
                String riskDangerPointId = createOrUpdateRiskDangerPointRequestRs.getRiskDangerPointId();
                if (StringUtils.isNotBlank(riskDangerPointId)) {
                    paramRiskDangerPointIdSet.add(riskDangerPointId);
                }
                paramIndicatorInstanceIdSet.add(createOrUpdateRiskDangerPointRequestRs.getIndicatorInstanceId());
            });
        });
        if (!paramRiskDeathModelIdSet.isEmpty()) {
            riskDeathModelService.lambdaQuery()
                .eq(RiskDeathModelEntity::getAppId, appId)
                .in(RiskDeathModelEntity::getRiskDeathModelId, paramRiskDeathModelIdSet)
                .list()
                .forEach(riskDeathModelEntity -> {
                    String riskModelId1 = riskDeathModelEntity.getRiskModelId();
                    dbRiskDeathModelIdSet.add(riskModelId1);
                    kRiskDeathModelIdVRiskDeathModelMap.put(riskModelId1, riskDeathModelEntity);
                });
            if (
                paramRiskDeathModelIdSet.stream().anyMatch(riskDeathModelId -> !dbRiskDeathModelIdSet.contains(riskDeathModelId))
            ) {
                log.warn("method RiskModelBiz.createOrUpdateRs param createOrUpdateRiskModelRequestRs riskModelIdSet:{} is illegal", paramRiskDeathModelIdSet);
                throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
            }
        }
        if (!paramRiskDangerPointIdSet.isEmpty()) {
            riskDangerPointService.lambdaQuery()
                .eq(RiskDangerPointEntity::getAppId, appId)
                .in(RiskDangerPointEntity::getRiskDangerPointId, paramRiskDangerPointIdSet)
                .list()
                .forEach(riskDangerPointEntity -> {
                    String riskDangerPointId = riskDangerPointEntity.getRiskDangerPointId();
                    dbRiskDangerPointIdSet.add(riskDangerPointId);
                    kRiskDangerPointIdVRiskDangerPointMap.put(riskDangerPointId, riskDangerPointEntity);
                });
            if (
                paramRiskDangerPointIdSet.stream().anyMatch(riskDangerPointId -> !dbRiskDangerPointIdSet.contains(riskDangerPointId))
            ) {
                log.warn("method RiskModelBiz.createOrUpdateRs param createOrUpdateRiskModelRequestRs riskDangerPointIdSet:{} is illegal", paramRiskDangerPointIdSet);
                throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
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
                log.warn("method RiskModelBiz.createOrUpdateRs param createOrUpdateRiskModelRequestRs indicatorInstanceIdSet:{} is illegal", paramIndicatorInstanceIdSet);
                throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
            }
        }
        createOrUpdateRiskDeathModelRequestRsList.forEach(createOrUpdateRiskDeathModelRequestRs -> {
            String riskDeathModelId = createOrUpdateRiskDeathModelRequestRs.getRiskDeathModelId();
            String riskModelId2 = createOrUpdateRiskDeathModelRequestRs.getRiskModelId();
            String riskDeathReasonName = createOrUpdateRiskDeathModelRequestRs.getRiskDeathReasonName();
            Integer riskDeathProbability = createOrUpdateRiskDeathModelRequestRs.getRiskDeathProbability();
            if (StringUtils.isBlank(riskDeathModelId)) {
                riskDeathModelId = idGenerator.nextIdStr();
                finalRiskDeathModelEntityList.add(RiskDeathModelEntity
                    .builder()
                    .riskDeathModelId(riskDeathModelId)
                    .appId(appId)
                    .riskModelId(riskModelId2)
                    .riskDeathReasonName(riskDeathReasonName)
                    .riskDeathProbability(riskDeathProbability)
                    .build());
            } else {
                RiskDeathModelEntity riskDeathModelEntity = kRiskDeathModelIdVRiskDeathModelMap.get(riskDeathModelId);
                riskDeathModelEntity.setRiskDeathReasonName(riskDeathReasonName);
                riskDeathModelEntity.setRiskDeathProbability(riskDeathProbability);
                finalRiskDeathModelEntityList.add(riskDeathModelEntity);
            }
            List<CreateOrUpdateRiskDangerPointRequestRs> createOrUpdateRiskDangerPointRequestRsList = createOrUpdateRiskDeathModelRequestRs.getCreateOrUpdateRiskDangerPointRequestRsList();
            createOrUpdateRiskDangerPointRequestRsList.forEach(createOrUpdateRiskDangerPointRequestRs -> {
                String riskDangerPointId = createOrUpdateRiskDangerPointRequestRs.getRiskDangerPointId();
                String riskDeathModelId2 = createOrUpdateRiskDangerPointRequestRs.getRiskDeathModelId();
                String indicatorInstanceId = createOrUpdateRiskDangerPointRequestRs.getIndicatorInstanceId();
                String expression = createOrUpdateRiskDangerPointRequestRs.getExpression();
                if (StringUtils.isBlank(riskDangerPointId)) {
                    riskDangerPointId = idGenerator.nextIdStr();
                    finalRiskDangerPointEntityList.add(RiskDangerPointEntity
                        .builder()
                        .riskDangerPointId(riskDangerPointId)
                        .appId(appId)
                        .riskDeathModelId(riskDeathModelId2)
                        .indicatorInstanceId(indicatorInstanceId)
                        .expression(expression)
                        .build()
                    );
                } else {
                    RiskDangerPointEntity riskDangerPointEntity = kRiskDangerPointIdVRiskDangerPointMap.get(riskDangerPointId);
                    riskDangerPointEntity.setRiskDeathModelId(indicatorInstanceId);
                    riskDangerPointEntity.setExpression(expression);
                    finalRiskDangerPointEntityList.add(riskDangerPointEntity);
                }
            });
        });
        riskDangerPointService.saveOrUpdateBatch(finalRiskDangerPointEntityList);
        riskDeathModelService.saveOrUpdateBatch(finalRiskDeathModelEntityList);
        Integer riskDeathProbability = RiskModelBiz.computeRiskModelRiskDeathProbability(finalRiskDeathModelEntityList);
        finalRiskModelEntity.setRiskDeathProbability(riskDeathProbability);
        riskModelService.saveOrUpdate(finalRiskModelEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> riskModelIdList) {
        if (Objects.isNull(riskModelIdList) || riskModelIdList.isEmpty()) {
            log.warn("method RiskModelBiz.batchDeleteRs param riskModelIdList is empty");
            throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbRiskModelIdSet = riskModelService.lambdaQuery()
            .in(RiskModelEntity::getRiskModelId, riskModelIdList)
            .list()
            .stream()
            .map(RiskModelEntity::getRiskModelId)
            .collect(Collectors.toSet());
        if (
            riskModelIdList.stream().anyMatch(riskModelId -> !dbRiskModelIdSet.contains(riskModelId))
        ) {
            log.warn("method RiskModelBiz.batchDeleteRs param riskModelIdList:{} is illegal", riskModelIdList);
            throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = riskModelService.remove(
            new LambdaQueryWrapper<RiskModelEntity>()
                .in(RiskModelEntity::getRiskModelId, dbRiskModelIdSet)
        );
        if (!isRemove) {
            log.warn("method RiskModelBiz.batchDeleteRs param riskModelIdList:{} is illegal", riskModelIdList);
            throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
        }
        if (!dbRiskModelIdSet.isEmpty()) {
            Set<String> riskDeathModelIdSet = riskDeathModelService.lambdaQuery()
                .in(RiskDeathModelEntity::getRiskModelId, dbRiskModelIdSet)
                .list()
                .stream()
                .map(RiskDeathModelEntity::getRiskDeathModelId)
                .collect(Collectors.toSet());
            riskDeathModelService.remove(
                new LambdaQueryWrapper<RiskDeathModelEntity>()
                    .in(RiskDeathModelEntity::getRiskDeathModelId, dbRiskModelIdSet)
            );
            if (!riskDeathModelIdSet.isEmpty()) {
                riskDangerPointService.remove(
                    new LambdaQueryWrapper<RiskDangerPointEntity>()
                        .in(RiskDangerPointEntity::getRiskDeathModelId, riskDeathModelIdSet)
                );
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String riskModelId, Integer status) {
        RiskModelEntity riskModelEntity = riskModelService.lambdaQuery()
            .eq(RiskModelEntity::getRiskModelId, riskModelId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method RiskModelBiz.updateStatusRs param riskModelId:{} is illegal", riskModelId);
                throw new RiskModelException(EnumESC.VALIDATE_EXCEPTION);
            });
        riskModelEntity.setStatus(status);
        riskModelService.updateById(riskModelEntity);
    }

    public RiskModelResponseRs getRs(String riskModelId) {
        RiskModelEntity riskModelEntity = riskModelService.lambdaQuery()
            .eq(RiskModelEntity::getRiskModelId, riskModelId)
            .one();
        if (Objects.isNull(riskModelEntity)) {
            return null;
        }
        List<RiskModelEntity> riskModelEntityList = new ArrayList<>();
        riskModelEntityList.add(riskModelEntity);
        List<RiskModelResponseRs> riskModelResponseRsList = riskModelList2ResponseRsList(riskModelEntityList);
        if (riskModelResponseRsList.isEmpty()) {
            return null;
        }
        return riskModelResponseRsList.get(0);
    }

    public Page<RiskModelResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String name, String riskCategoryId, Integer status) {
        Page<RiskModelEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<RiskModelEntity> riskModelEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        riskModelEntityLambdaQueryWrapper
            .eq(Objects.nonNull(appId), RiskModelEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(riskCategoryId), RiskModelEntity::getRiskCategoryId, riskCategoryId)
            .eq(Objects.nonNull(status), RiskModelEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), RiskModelEntity::getName, StringUtils.isNotBlank(name) ? null : name.trim());
        Page<RiskModelEntity> riskModelEntityPage = riskModelService.page(page, riskModelEntityLambdaQueryWrapper);
        Page<RiskModelResponseRs> riskModelResponseRsPage = RsPageUtil.convertFromAnother(riskModelEntityPage);
        List<RiskModelEntity> riskModelEntityList = riskModelEntityPage.getRecords();
        List<RiskModelResponseRs> riskModelResponseRsList = riskModelList2ResponseRsList(riskModelEntityList);
        riskModelResponseRsPage.setRecords(riskModelResponseRsList);
        return riskModelResponseRsPage;
    }


//    /**
//    * @param
//    * @return
//    * @说明: 创建风险模型
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public void createRiskModel(CreateRiskModelRequest createRiskModel ) {
//
//    }
//    /**
//    * @param
//    * @return
//    * @说明: 删除风险模型
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public void deleteRiskModel(String riskModelId ) {
//
//    }
//    /**
//    * @param
//    * @return
//    * @说明: 更改风险模型
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public void updateRiskModel(UpdateRiskModelRequest updateRiskModel ) {
//
//    }
//    /**
//    * @param
//    * @return
//    * @说明: 更改启用状态
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public void updateStatusRiskModel(UpdateStatusRiskModelRequest updateStatusRiskModel ) {
//
//    }
//    /**
//    * @param
//    * @return
//    * @说明: 获取风险模型
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public RiskModelResponse getRiskModel(String riskModelId ) {
//        return new RiskModelResponse();
//    }
//    /**
//    * @param
//    * @return
//    * @说明: 筛选风险模型
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public List<RiskModelResponse> listRiskModel(String appId, String riskModelId, String modelName, Integer status ) {
//        return new ArrayList<RiskModelResponse>();
//    }
//    /**
//    * @param
//    * @return
//    * @说明: 分页筛选风险模型
//    * @关联表:
//    * @工时: 4H
//    * @开发者: runsix
//    * @开始时间:
//    * @创建时间: 2023年4月23日 上午9:44:34
//    */
//    public String pageRiskModel(Integer pageNo, Integer pageSize, String appId, String riskModelId, String modelName, Integer status ) {
//        return new String();
//    }
}