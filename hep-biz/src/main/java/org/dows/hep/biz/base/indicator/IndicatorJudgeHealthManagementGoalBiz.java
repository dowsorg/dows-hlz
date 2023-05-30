package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthManagementGoalResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorJudgeHealthManagementGoalException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.entity.IndicatorJudgeHealthManagementGoalEntity;
import org.dows.hep.entity.IndicatorJudgeHealthManagementGoalEntity;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.hep.service.IndicatorJudgeHealthManagementGoalService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:判断指标健管目标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeHealthManagementGoalBiz{
    private final IdGenerator idGenerator;
    private final IndicatorJudgeHealthManagementGoalService indicatorJudgeHealthManagementGoalService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorInstanceService indicatorInstanceService;

    private final IndicatorInstanceBiz indicatorInstanceBiz;

    public static IndicatorJudgeHealthManagementGoalResponseRs indicatorJudgeHealthManagementGoal2ResponseRs(
        IndicatorJudgeHealthManagementGoalEntity indicatorJudgeHealthManagementGoalEntity,
        IndicatorInstanceResponseRs indicatorInstanceResponseRs
    ) {
        if (Objects.isNull(indicatorJudgeHealthManagementGoalEntity)) {
            return null;
        }
        return IndicatorJudgeHealthManagementGoalResponseRs
            .builder()
            .id(indicatorJudgeHealthManagementGoalEntity.getId())
            .indicatorJudgeHealthManagementGoalId(indicatorJudgeHealthManagementGoalEntity.getIndicatorJudgeHealthManagementGoalId())
            .appId(indicatorJudgeHealthManagementGoalEntity.getAppId())
            .indicatorFuncId(indicatorJudgeHealthManagementGoalEntity.getIndicatorFuncId())
            .indicatorInstanceResponseRs(indicatorInstanceResponseRs)
            .point(indicatorJudgeHealthManagementGoalEntity.getPoint().doubleValue())
            .expression(indicatorJudgeHealthManagementGoalEntity.getExpression())
            .dt(indicatorJudgeHealthManagementGoalEntity.getDt())
            .build();
    }

    private List<IndicatorJudgeHealthManagementGoalResponseRs> indicatorJudgeHealthManagementGoalList2ResponseRsList(
        List<IndicatorJudgeHealthManagementGoalEntity> indicatorJudgeHealthManagementGoalEntityList
    ) {
        if (Objects.isNull(indicatorJudgeHealthManagementGoalEntityList) || indicatorJudgeHealthManagementGoalEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeHealthManagementGoalEntityList.get(0).getAppId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorJudgeHealthManagementGoalEntityList.forEach(
            indicatorJudgeHealthManagementGoalEntity -> {
                indicatorInstanceIdSet.add(indicatorJudgeHealthManagementGoalEntity.getIndicatorInstanceId());
            });
        Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceMap = new HashMap<>();
        if (!indicatorInstanceIdSet.isEmpty()) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                .list()
                .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity));
        }
        Map<String, IndicatorExpressionResponseRs> kIndicatorInstanceIdVIndicatorExpressionResponseRsMap = new HashMap<>();
        indicatorInstanceBiz.populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(appId, indicatorInstanceIdSet, kIndicatorInstanceIdVIndicatorExpressionResponseRsMap);
        return indicatorJudgeHealthManagementGoalEntityList
            .stream()
            .map(indicatorJudgeHealthManagementGoalEntity -> {
                String indicatorInstanceId = indicatorJudgeHealthManagementGoalEntity.getIndicatorInstanceId();
                IndicatorInstanceResponseRs indicatorInstanceResponseRs = IndicatorInstanceBiz.indicatorInstance2ResponseRs(
                    kIndicatorInstanceIdVIndicatorInstanceMap.get(indicatorInstanceId),
                    kIndicatorInstanceIdVIndicatorExpressionResponseRsMap.get(indicatorInstanceId)
                );
                return indicatorJudgeHealthManagementGoal2ResponseRs(indicatorJudgeHealthManagementGoalEntity, indicatorInstanceResponseRs);
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOrUpdateRs(BatchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs) {
        String appId = batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs.getAppId();
        String indicatorFuncId = batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs.getIndicatorFuncId();
        indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getAppId, appId)
            .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorJudgeHealthManagementGoalBiz.batchCreateOrUpdateRs param batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                throw new IndicatorJudgeHealthManagementGoalException(EnumESC.VALIDATE_EXCEPTION);
            });
        List<BatchCreateOrUpdateHealthManagementGoalDTO> batchCreateOrUpdateHealthManagementGoalDTOList = batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs.getBatchCreateOrUpdateHealthManagementGoalDTOList();
        Map<String, IndicatorJudgeHealthManagementGoalEntity> kIdVIndicatorJudgeHealthManagementGoalEntityMap = new HashMap<>();
        Set<String> paramIndicatorJudgeHealthManagementGoalIdSet = new HashSet<>();
        Set<String> dbIndicatorJudgeHealthManagementGoalIdSet = new HashSet<>();
        Set<String> paramIndicatorInstanceIdSet = new HashSet<>();
        Set<String> dbIndicatorInstanceIdSet = new HashSet<>();
        batchCreateOrUpdateHealthManagementGoalDTOList.forEach(batchCreateOrUpdateHealthManagementGoalDTO -> {
            paramIndicatorJudgeHealthManagementGoalIdSet.add(batchCreateOrUpdateHealthManagementGoalDTO.getIndicatorJudgeHealthManagementGoalId());
            paramIndicatorInstanceIdSet.add(batchCreateOrUpdateHealthManagementGoalDTO.getIndicatorInstanceId());
        });
        if (!paramIndicatorJudgeHealthManagementGoalIdSet.isEmpty()) {
            indicatorJudgeHealthManagementGoalService.lambdaQuery()
                .eq(IndicatorJudgeHealthManagementGoalEntity::getAppId, appId)
                .in(IndicatorJudgeHealthManagementGoalEntity::getIndicatorJudgeHealthManagementGoalId, dbIndicatorJudgeHealthManagementGoalIdSet)
                .list()
                .forEach(indicatorJudgeHealthManagementGoalEntity -> {
                    dbIndicatorJudgeHealthManagementGoalIdSet.add(indicatorJudgeHealthManagementGoalEntity.getIndicatorJudgeHealthManagementGoalId());
                    kIdVIndicatorJudgeHealthManagementGoalEntityMap.put(indicatorJudgeHealthManagementGoalEntity.getIndicatorJudgeHealthManagementGoalId(), indicatorJudgeHealthManagementGoalEntity);
                });
            if (
                paramIndicatorJudgeHealthManagementGoalIdSet.stream().anyMatch(id -> !dbIndicatorJudgeHealthManagementGoalIdSet.contains(id))
            ) {
                log.warn("method IndicatorJudgeHealthManagementGoalBiz.batchCreateOrUpdateRs param batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs indicatorJudgeHealthManagementGoalId:{} is illegal", paramIndicatorJudgeHealthManagementGoalIdSet);
                throw new IndicatorJudgeHealthManagementGoalException(EnumESC.VALIDATE_EXCEPTION);
            }
        }
        if (!paramIndicatorInstanceIdSet.isEmpty()) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .in(IndicatorInstanceEntity::getIndicatorInstanceId, paramIndicatorInstanceIdSet)
                .list()
                .forEach(indicatorInstanceEntity -> {
                    dbIndicatorInstanceIdSet.add(indicatorInstanceEntity.getIndicatorInstanceId());
                });
            if (
                paramIndicatorInstanceIdSet.stream().anyMatch(id -> !dbIndicatorInstanceIdSet.contains(id))
            ) {
                log.warn("method IndicatorJudgeHealthManagementGoalBiz.batchCreateOrUpdateRs param batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs indicatorInstanceId:{} is illegal", paramIndicatorInstanceIdSet);
                throw new IndicatorJudgeHealthManagementGoalException(EnumESC.VALIDATE_EXCEPTION);
            }
        }
        List<IndicatorJudgeHealthManagementGoalEntity> finalIndicatorJudgeHealthManagementGoalEntityList = new ArrayList<>();
        batchCreateOrUpdateHealthManagementGoalDTOList.forEach(batchCreateOrUpdateHealthManagementGoalDTO -> {
            String indicatorJudgeHealthManagementGoalId = batchCreateOrUpdateHealthManagementGoalDTO.getIndicatorJudgeHealthManagementGoalId();
            String indicatorInstanceId = batchCreateOrUpdateHealthManagementGoalDTO.getIndicatorInstanceId();
            BigDecimal point = batchCreateOrUpdateHealthManagementGoalDTO.getPoint();
            String expression = batchCreateOrUpdateHealthManagementGoalDTO.getExpression();
            if (StringUtils.isBlank(indicatorJudgeHealthManagementGoalId)) {
                finalIndicatorJudgeHealthManagementGoalEntityList.add(IndicatorJudgeHealthManagementGoalEntity
                    .builder()
                    .indicatorJudgeHealthManagementGoalId(idGenerator.nextIdStr())
                    .appId(appId)
                    .indicatorFuncId(indicatorFuncId)
                    .indicatorInstanceId(indicatorInstanceId)
                    .point(point)
                    .expression(expression)
                    .build()
                );
            } else {
                IndicatorJudgeHealthManagementGoalEntity indicatorJudgeHealthManagementGoalEntity = kIdVIndicatorJudgeHealthManagementGoalEntityMap.get(indicatorJudgeHealthManagementGoalId);
                indicatorJudgeHealthManagementGoalEntity.setIndicatorInstanceId(indicatorInstanceId);
                indicatorJudgeHealthManagementGoalEntity.setPoint(point);
                indicatorJudgeHealthManagementGoalEntity.setExpression(expression);
                finalIndicatorJudgeHealthManagementGoalEntityList.add(indicatorJudgeHealthManagementGoalEntity);
            }
        });
        indicatorJudgeHealthManagementGoalService.saveOrUpdateBatch(finalIndicatorJudgeHealthManagementGoalEntityList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorJudgeHealthManagementGoalIdList) {
        if (Objects.isNull(indicatorJudgeHealthManagementGoalIdList) || indicatorJudgeHealthManagementGoalIdList.isEmpty()) {
            log.warn("method IndicatorJudgeHealthManagementGoalBiz.batchDeleteRs param indicatorJudgeHealthManagementGoalIdList is empty");
            throw new IndicatorJudgeHealthManagementGoalException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorJudgeHealthManagementGoalIdSet = indicatorJudgeHealthManagementGoalService.lambdaQuery()
            .in(IndicatorJudgeHealthManagementGoalEntity::getIndicatorJudgeHealthManagementGoalId, indicatorJudgeHealthManagementGoalIdList)
            .list()
            .stream()
            .map(IndicatorJudgeHealthManagementGoalEntity::getIndicatorJudgeHealthManagementGoalId)
            .collect(Collectors.toSet());
        if (
            indicatorJudgeHealthManagementGoalIdList.stream().anyMatch(id -> !dbIndicatorJudgeHealthManagementGoalIdSet.contains(id))
        ) {
            log.warn("method IndicatorJudgeHealthManagementGoalBiz.batchDeleteRs param indicatorJudgeHealthManagementGoalIdList:{} is illegal", indicatorJudgeHealthManagementGoalIdList);
            throw new IndicatorJudgeHealthManagementGoalException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemoved = indicatorJudgeHealthManagementGoalService.remove(
            new LambdaQueryWrapper<IndicatorJudgeHealthManagementGoalEntity>()
                .in(IndicatorJudgeHealthManagementGoalEntity::getIndicatorJudgeHealthManagementGoalId, dbIndicatorJudgeHealthManagementGoalIdSet)
        );
        if (!isRemoved) {
            log.warn("method IndicatorJudgeHealthManagementGoalBiz.batchDeleteRs param indicatorJudgeHealthManagementGoalIdList:{} is illegal", indicatorJudgeHealthManagementGoalIdList);
            throw new IndicatorJudgeHealthManagementGoalException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    public IndicatorJudgeHealthManagementGoalResponseRs getRs(String indicatorJudgeHealthManagementGoalId) {
        IndicatorJudgeHealthManagementGoalEntity indicatorJudgeHealthManagementGoalEntity = indicatorJudgeHealthManagementGoalService.lambdaQuery()
            .eq(IndicatorJudgeHealthManagementGoalEntity::getIndicatorJudgeHealthManagementGoalId, indicatorJudgeHealthManagementGoalId)
            .one();
        if (Objects.isNull(indicatorJudgeHealthManagementGoalEntity)) {
            return null;
        }
        List<IndicatorJudgeHealthManagementGoalEntity> indicatorJudgeHealthManagementGoalEntityList = new ArrayList<>();
        indicatorJudgeHealthManagementGoalEntityList.add(indicatorJudgeHealthManagementGoalEntity);
        List<IndicatorJudgeHealthManagementGoalResponseRs> indicatorJudgeHealthManagementGoalResponseRs = indicatorJudgeHealthManagementGoalList2ResponseRsList(indicatorJudgeHealthManagementGoalEntityList);
        if (indicatorJudgeHealthManagementGoalResponseRs.isEmpty()) {
            return null;
        }
        return indicatorJudgeHealthManagementGoalResponseRs.get(0);
    }

    public List<IndicatorJudgeHealthManagementGoalResponseRs> getRsByIndicatorFuncId(String indicatorFuncId) {
        List<IndicatorJudgeHealthManagementGoalEntity> indicatorJudgeHealthManagementGoalEntityList = indicatorJudgeHealthManagementGoalService.lambdaQuery()
            .eq(IndicatorJudgeHealthManagementGoalEntity::getIndicatorFuncId, indicatorFuncId)
            .list();
        return indicatorJudgeHealthManagementGoalList2ResponseRsList(indicatorJudgeHealthManagementGoalEntityList);
    }

    /**
    * @param
    * @return
    * @说明: 创建判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeHealthManagementGoal(CreateIndicatorJudgeHealthManagementGoalRequest createIndicatorJudgeHealthManagementGoal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeHealthManagementGoal(String indicatorJudgeHealthManagementGoalId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeHealthManagementGoalRequest updateStatusIndicatorJudgeHealthManagementGoal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeHealthManagementGoal(UpdateIndicatorJudgeHealthManagementGoalRequest updateIndicatorJudgeHealthManagementGoal ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeHealthManagementGoalResponse getIndicatorJudgeHealthManagementGoal(String indicatorJudgeHealthManagementGoalId ) {
        return new IndicatorJudgeHealthManagementGoalResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeHealthManagementGoalResponse> listIndicatorJudgeHealthManagementGoal(String appId, String indicatorCategoryId, DecimalRequest point, Integer status ) {
        return new ArrayList<IndicatorJudgeHealthManagementGoalResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健管目标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeHealthManagementGoal(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, DecimalRequest point, Integer status ) {
        return new String();
    }
}