package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorJudgeHealthGuidanceException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:判断指标健康指导
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeHealthGuidanceBiz{
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;

    public static IndicatorJudgeHealthGuidanceResponseRs indicatorJudgeHealthGuidance2ResponseRs(
        IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity,
        IndicatorCategoryResponse indicatorCategoryResponse
    ) {
        return IndicatorJudgeHealthGuidanceResponseRs
            .builder()
            .id(indicatorJudgeHealthGuidanceEntity.getId())
            .indicatorJudgeHealthGuidanceId(indicatorJudgeHealthGuidanceEntity.getIndicatorJudgeHealthGuidanceId())
            .appId(indicatorJudgeHealthGuidanceEntity.getAppId())
            .indicatorFuncId(indicatorJudgeHealthGuidanceEntity.getIndicatorFuncId())
            .name(indicatorJudgeHealthGuidanceEntity.getName())
            .indicatorCategoryResponse(indicatorCategoryResponse)
            .point(indicatorJudgeHealthGuidanceEntity.getPoint().doubleValue())
            .expression(indicatorJudgeHealthGuidanceEntity.getExpression())
            .resultExplain(indicatorJudgeHealthGuidanceEntity.getResultExplain())
            .status(indicatorJudgeHealthGuidanceEntity.getStatus())
            .dt(indicatorJudgeHealthGuidanceEntity.getDt())
            .build();
    }

    private List<IndicatorJudgeHealthGuidanceResponseRs> indicatorJudgeHealthGuidanceEntityList2ResponseRsList(
        List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList
    ) {
        if (Objects.isNull(indicatorJudgeHealthGuidanceEntityList) || indicatorJudgeHealthGuidanceEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeHealthGuidanceEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        indicatorJudgeHealthGuidanceEntityList.forEach(
            indicatorJudgeHealthGuidanceEntity -> {
                indicatorCategoryIdSet.add(indicatorJudgeHealthGuidanceEntity.getIndicatorCategoryId());
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
        return indicatorJudgeHealthGuidanceEntityList
            .stream()
            .map(indicatorJudgeHealthGuidanceEntity -> {
                IndicatorCategoryResponse indicatorCategoryResponse = kIndicatorCategoryIdVIndicatorCategoryResponseMap.get(indicatorJudgeHealthGuidanceEntity.getIndicatorCategoryId());
                return indicatorJudgeHealthGuidance2ResponseRs(
                    indicatorJudgeHealthGuidanceEntity,
                    indicatorCategoryResponse
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorJudgeHealthGuidanceRequestRs createOrUpdateIndicatorJudgeHealthGuidanceRequestRs) {
        IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity;
        String appId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthGuidanceBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthGuidanceRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthGuidanceBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthGuidanceRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorJudgeHealthGuidanceId = createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getIndicatorJudgeHealthGuidanceId();
        BigDecimal point = BigDecimal.valueOf(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getPoint());
        if (StringUtils.isBlank(indicatorJudgeHealthGuidanceId)) {
            indicatorJudgeHealthGuidanceEntity = IndicatorJudgeHealthGuidanceEntity
                .builder()
                .indicatorJudgeHealthGuidanceId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .point(point)
                .resultExplain(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getResultExplain())
                .build();
        } else {
            indicatorJudgeHealthGuidanceEntity = indicatorJudgeHealthGuidanceService.lambdaQuery()
                .eq(IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
                .eq(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, indicatorJudgeHealthGuidanceId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthGuidanceBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthGuidanceRequestRs indicatorJudgeHealthGuidanceId:{} is illegal", indicatorJudgeHealthGuidanceId);
                    throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorJudgeHealthGuidanceEntity.setName(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getName());
            indicatorJudgeHealthGuidanceEntity.setStatus(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getStatus());
            indicatorJudgeHealthGuidanceEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorJudgeHealthGuidanceEntity.setPoint(point);
            indicatorJudgeHealthGuidanceEntity.setExpression(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getExpression());
            indicatorJudgeHealthGuidanceEntity.setResultExplain(createOrUpdateIndicatorJudgeHealthGuidanceRequestRs.getResultExplain());
        }
        indicatorJudgeHealthGuidanceService.saveOrUpdate(indicatorJudgeHealthGuidanceEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorJudgeHealthGuidanceIdList) {
        if (Objects.isNull(indicatorJudgeHealthGuidanceIdList) || indicatorJudgeHealthGuidanceIdList.isEmpty()) {
            log.warn("method IndicatorJudgeHealthGuidanceBiz.batchDeleteRs param indicatorJudgeHealthGuidanceIdList is empty");
            throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorJudgeHealthGuidanceIdSet = indicatorJudgeHealthGuidanceService.lambdaQuery()
            .in(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, indicatorJudgeHealthGuidanceIdList)
            .list()
            .stream()
            .map(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId)
            .collect(Collectors.toSet());
        if (
            indicatorJudgeHealthGuidanceIdList.stream().anyMatch(indicatorJudgeHealthGuidanceId -> !dbIndicatorJudgeHealthGuidanceIdSet.contains(indicatorJudgeHealthGuidanceId))
        ) {
            log.warn("method IndicatorJudgeHealthGuidanceBiz.batchDeleteRs param indicatorJudgeHealthGuidanceIdList:{} is illegal", indicatorJudgeHealthGuidanceIdList);
            throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = indicatorJudgeHealthGuidanceService.remove(
            new LambdaQueryWrapper<IndicatorJudgeHealthGuidanceEntity>()
                .in(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, dbIndicatorJudgeHealthGuidanceIdSet)
        );
        if (!isRemove) {
            log.warn("method IndicatorJudgeHealthGuidanceBiz.batchDeleteRs param indicatorJudgeHealthGuidanceIdList:{} is illegal", indicatorJudgeHealthGuidanceIdList);
            throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String indicatorJudgeHealthGuidanceId, Integer status) {
        IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity = indicatorJudgeHealthGuidanceService.lambdaQuery()
            .eq(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, indicatorJudgeHealthGuidanceId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorJudgeHealthGuidanceBiz.updateStatusRs param indicatorJudgeHealthGuidanceId:{} is illegal", indicatorJudgeHealthGuidanceId);
                throw new IndicatorJudgeHealthGuidanceException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorJudgeHealthGuidanceEntity.setStatus(status);
        indicatorJudgeHealthGuidanceService.updateById(indicatorJudgeHealthGuidanceEntity);
    }

    public IndicatorJudgeHealthGuidanceResponseRs getRs(String indicatorJudgeHealthGuidanceId) {
        IndicatorJudgeHealthGuidanceEntity indicatorJudgeHealthGuidanceEntity = indicatorJudgeHealthGuidanceService.lambdaQuery()
            .eq(IndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId, indicatorJudgeHealthGuidanceId)
            .one();
        if (Objects.isNull(indicatorJudgeHealthGuidanceEntity)) {
            return null;
        }
        List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList = new ArrayList<>();
        indicatorJudgeHealthGuidanceEntityList.add(indicatorJudgeHealthGuidanceEntity);
        List<IndicatorJudgeHealthGuidanceResponseRs> indicatorJudgeHealthGuidanceResponseRsList = indicatorJudgeHealthGuidanceEntityList2ResponseRsList(indicatorJudgeHealthGuidanceEntityList);
        return indicatorJudgeHealthGuidanceResponseRsList.get(0);
    }

    public IPage<IndicatorJudgeHealthGuidanceResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String paramIndicatorCategoryId, Integer status) {
        Page<IndicatorJudgeHealthGuidanceEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityLQW = new LambdaQueryWrapper<>();
        indicatorJudgeHealthGuidanceEntityLQW
            .eq(Objects.nonNull(appId), IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorJudgeHealthGuidanceEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(StringUtils.isNotBlank(paramIndicatorCategoryId), IndicatorJudgeHealthGuidanceEntity::getIndicatorCategoryId, paramIndicatorCategoryId)
            .eq(Objects.nonNull(status), IndicatorJudgeHealthGuidanceEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorJudgeHealthGuidanceEntity::getName, StringUtils.isNotBlank(name) ? null : name.trim());
        Page<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityPage = indicatorJudgeHealthGuidanceService.page(page, indicatorJudgeHealthGuidanceEntityLQW);
        Page<IndicatorJudgeHealthGuidanceResponseRs> indicatorJudgeHealthGuidanceResponseRsPage = RsPageUtil.convertFromAnother(indicatorJudgeHealthGuidanceEntityPage);
        List<IndicatorJudgeHealthGuidanceEntity> indicatorJudgeHealthGuidanceEntityList = indicatorJudgeHealthGuidanceEntityPage.getRecords();
        List<IndicatorJudgeHealthGuidanceResponseRs> indicatorJudgeHealthGuidanceResponseRsList = indicatorJudgeHealthGuidanceEntityList2ResponseRsList(indicatorJudgeHealthGuidanceEntityList);
        indicatorJudgeHealthGuidanceResponseRsPage.setRecords(indicatorJudgeHealthGuidanceResponseRsList);
        return indicatorJudgeHealthGuidanceResponseRsPage;
    }
    
    /**
    * @param
    * @return
    * @说明: 创建判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeHealthGuidance(CreateIndicatorJudgeHealthGuidanceRequest createIndicatorJudgeHealthGuidance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeHealthGuidance(String indicatorJudgeHealthGuidanceId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeHealthGuidanceRequest updateStatusIndicatorJudgeHealthGuidance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeHealthGuidance(UpdateIndicatorJudgeHealthGuidanceRequest updateIndicatorJudgeHealthGuidance ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeHealthGuidanceResponse getIndicatorJudgeHealthGuidance(String indicatorJudgeHealthGuidanceId ) {
        return new IndicatorJudgeHealthGuidanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeHealthGuidanceResponse> listIndicatorJudgeHealthGuidance(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeHealthGuidanceResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标健康指导
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeHealthGuidance(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}