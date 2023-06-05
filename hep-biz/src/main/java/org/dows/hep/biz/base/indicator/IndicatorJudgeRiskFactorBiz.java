package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionType;
import org.dows.hep.api.exception.IndicatorJudgeRiskFactorException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorJudgeRiskFactorService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:判断指标危险因素
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeRiskFactorBiz{
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorJudgeRiskFactorService indicatorJudgeRiskFactorService;

    private final IndicatorExpressionBiz indicatorExpressionBiz;

    public static IndicatorJudgeRiskFactorResponseRs indicatorJudgeRiskFactor2ResponseRs(
        IndicatorJudgeRiskFactorEntity indicatorJudgeRiskFactorEntity,
        IndicatorCategoryResponse indicatorCategoryResponse,
        IndicatorExpressionResponseRs indicatorExpressionResponseRs
    ) {
        return IndicatorJudgeRiskFactorResponseRs
            .builder()
            .id(indicatorJudgeRiskFactorEntity.getId())
            .indicatorJudgeRiskFactorId(indicatorJudgeRiskFactorEntity.getIndicatorJudgeRiskFactorId())
            .appId(indicatorJudgeRiskFactorEntity.getAppId())
            .indicatorFuncId(indicatorJudgeRiskFactorEntity.getIndicatorFuncId())
            .name(indicatorJudgeRiskFactorEntity.getName())
            .indicatorCategoryResponse(indicatorCategoryResponse)
            .point(indicatorJudgeRiskFactorEntity.getPoint().doubleValue())
            .expression(indicatorJudgeRiskFactorEntity.getExpression())
            .resultExplain(indicatorJudgeRiskFactorEntity.getResultExplain())
            .status(indicatorJudgeRiskFactorEntity.getStatus())
            .dt(indicatorJudgeRiskFactorEntity.getDt())
            .indicatorExpressionResponseRs(indicatorExpressionResponseRs)
            .build();
    }

    private List<IndicatorJudgeRiskFactorResponseRs> indicatorJudgeRiskFactorEntityList2ResponseRsList(
        List<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityList
    ) {
        if (Objects.isNull(indicatorJudgeRiskFactorEntityList) || indicatorJudgeRiskFactorEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeRiskFactorEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        Set<String> principalIdSet = new HashSet<>();
        indicatorJudgeRiskFactorEntityList.forEach(
            indicatorJudgeRiskFactorEntity -> {
                indicatorCategoryIdSet.add(indicatorJudgeRiskFactorEntity.getIndicatorCategoryId());
                principalIdSet.add(indicatorJudgeRiskFactorEntity.getIndicatorJudgeRiskFactorId());
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
        Map<String, IndicatorExpressionResponseRs> kPrincipalIdVIndicatorExpressionResponseRsMap = new HashMap<>();
        indicatorExpressionBiz.populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(appId, principalIdSet, kPrincipalIdVIndicatorExpressionResponseRsMap);
        return indicatorJudgeRiskFactorEntityList
            .stream()
            .map(indicatorJudgeRiskFactorEntity -> {
                IndicatorCategoryResponse indicatorCategoryResponse = kIndicatorCategoryIdVIndicatorCategoryResponseMap.get(indicatorJudgeRiskFactorEntity.getIndicatorCategoryId());
                IndicatorExpressionResponseRs indicatorExpressionResponseRs = kPrincipalIdVIndicatorExpressionResponseRsMap.get(indicatorJudgeRiskFactorEntity.getIndicatorJudgeRiskFactorId());
                return indicatorJudgeRiskFactor2ResponseRs(
                    indicatorJudgeRiskFactorEntity,
                    indicatorCategoryResponse,
                    indicatorExpressionResponseRs
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorJudgeRiskFactorRequestRs createOrUpdateIndicatorJudgeRiskFactorRequestRs) {
        IndicatorJudgeRiskFactorEntity indicatorJudgeRiskFactorEntity;
        String appId = createOrUpdateIndicatorJudgeRiskFactorRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorJudgeRiskFactorRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeRiskFactorBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeRiskFactorRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorJudgeRiskFactorRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeRiskFactorBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeRiskFactorRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorJudgeRiskFactorId = createOrUpdateIndicatorJudgeRiskFactorRequestRs.getIndicatorJudgeRiskFactorId();
        BigDecimal point = BigDecimal.valueOf(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getPoint());
        if (StringUtils.isBlank(indicatorJudgeRiskFactorId)) {
            indicatorJudgeRiskFactorId = idGenerator.nextIdStr();
            indicatorJudgeRiskFactorEntity = IndicatorJudgeRiskFactorEntity
                .builder()
                .indicatorJudgeRiskFactorId(indicatorJudgeRiskFactorId)
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .point(point)
                .resultExplain(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getResultExplain())
                .status(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getStatus())
                .build();
        } else {
            String finalIndicatorJudgeRiskFactorId = indicatorJudgeRiskFactorId;
            indicatorJudgeRiskFactorEntity = indicatorJudgeRiskFactorService.lambdaQuery()
                .eq(IndicatorJudgeRiskFactorEntity::getAppId, appId)
                .eq(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId, indicatorJudgeRiskFactorId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeRiskFactorBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeRiskFactorRequestRs indicatorJudgeRiskFactorId:{} is illegal", finalIndicatorJudgeRiskFactorId);
                    throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorJudgeRiskFactorEntity.setName(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getName());
            indicatorJudgeRiskFactorEntity.setStatus(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getStatus());
            indicatorJudgeRiskFactorEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorJudgeRiskFactorEntity.setPoint(point);
            indicatorJudgeRiskFactorEntity.setExpression(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getExpression());
            indicatorJudgeRiskFactorEntity.setResultExplain(createOrUpdateIndicatorJudgeRiskFactorRequestRs.getResultExplain());
        }
        indicatorJudgeRiskFactorService.saveOrUpdate(indicatorJudgeRiskFactorEntity);
        CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs = createOrUpdateIndicatorJudgeRiskFactorRequestRs.getCreateOrUpdateIndicatorExpressionRequestRs();
        if (Objects.nonNull(createOrUpdateIndicatorExpressionRequestRs)) {
            createOrUpdateIndicatorExpressionRequestRs.setType(EnumIndicatorExpressionType.INDICATOR_JUDGE_RISK_FACTOR.getType());
            createOrUpdateIndicatorExpressionRequestRs.setPrincipalId(indicatorJudgeRiskFactorId);
        }
        indicatorExpressionBiz.createOrUpdate(createOrUpdateIndicatorExpressionRequestRs);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorJudgeRiskFactorIdList) {
        if (Objects.isNull(indicatorJudgeRiskFactorIdList) || indicatorJudgeRiskFactorIdList.isEmpty()) {
            log.warn("method IndicatorJudgeRiskFactorBiz.batchDeleteRs param indicatorJudgeRiskFactorIdList is empty");
            throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorJudgeRiskFactorIdSet = indicatorJudgeRiskFactorService.lambdaQuery()
            .in(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId, indicatorJudgeRiskFactorIdList)
            .list()
            .stream()
            .map(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId)
            .collect(Collectors.toSet());
        if (
            indicatorJudgeRiskFactorIdList.stream().anyMatch(indicatorJudgeRiskFactorId -> !dbIndicatorJudgeRiskFactorIdSet.contains(indicatorJudgeRiskFactorId))
        ) {
            log.warn("method IndicatorJudgeRiskFactorBiz.batchDeleteRs param indicatorJudgeRiskFactorIdList:{} is illegal", indicatorJudgeRiskFactorIdList);
            throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = indicatorJudgeRiskFactorService.remove(
            new LambdaQueryWrapper<IndicatorJudgeRiskFactorEntity>()
                .in(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId, dbIndicatorJudgeRiskFactorIdSet)
        );
        if (!isRemove) {
            log.warn("method IndicatorJudgeRiskFactorBiz.batchDeleteRs param indicatorJudgeRiskFactorIdList:{} is illegal", indicatorJudgeRiskFactorIdList);
            throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String indicatorJudgeRiskFactorId, Integer status) {
        IndicatorJudgeRiskFactorEntity indicatorJudgeRiskFactorEntity = indicatorJudgeRiskFactorService.lambdaQuery()
            .eq(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId, indicatorJudgeRiskFactorId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorJudgeRiskFactorBiz.updateStatusRs param indicatorJudgeRiskFactorId:{} is illegal", indicatorJudgeRiskFactorId);
                throw new IndicatorJudgeRiskFactorException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorJudgeRiskFactorEntity.setStatus(status);
        indicatorJudgeRiskFactorService.updateById(indicatorJudgeRiskFactorEntity);
    }

    public IndicatorJudgeRiskFactorResponseRs getRs(String indicatorJudgeRiskFactorId) {
        IndicatorJudgeRiskFactorEntity indicatorJudgeRiskFactorEntity = indicatorJudgeRiskFactorService.lambdaQuery()
            .eq(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId, indicatorJudgeRiskFactorId)
            .one();
        if (Objects.isNull(indicatorJudgeRiskFactorEntity)) {
            return null;
        }
        List<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityList = new ArrayList<>();
        indicatorJudgeRiskFactorEntityList.add(indicatorJudgeRiskFactorEntity);
        List<IndicatorJudgeRiskFactorResponseRs> indicatorJudgeRiskFactorResponseRsList = indicatorJudgeRiskFactorEntityList2ResponseRsList(indicatorJudgeRiskFactorEntityList);
        if (indicatorJudgeRiskFactorResponseRsList.isEmpty()) {
            return null;
        }
        return indicatorJudgeRiskFactorResponseRsList.get(0);
    }

    public Page<IndicatorJudgeRiskFactorResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
        Page<IndicatorJudgeRiskFactorEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityLQW = new LambdaQueryWrapper<>();
        indicatorJudgeRiskFactorEntityLQW
            .eq(Objects.nonNull(appId), IndicatorJudgeRiskFactorEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorJudgeRiskFactorEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(Objects.nonNull(status), IndicatorJudgeRiskFactorEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorJudgeRiskFactorEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
        if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
            Set<String> firstIndicatorCategoryIdSet = Arrays.stream(indicatorCategoryIdList.split(",")).collect(Collectors.toSet());
            /* runsix:if first category list mapped second category list is empty, means nothing */
            if (firstIndicatorCategoryIdSet.isEmpty()) {
                return RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
            } else {
                indicatorJudgeRiskFactorEntityLQW.in(IndicatorJudgeRiskFactorEntity::getIndicatorCategoryId, firstIndicatorCategoryIdSet);
            }
        }
        Page<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityPage = indicatorJudgeRiskFactorService.page(page, indicatorJudgeRiskFactorEntityLQW);
        Page<IndicatorJudgeRiskFactorResponseRs> indicatorJudgeRiskFactorResponseRsPage = RsPageUtil.convertFromAnother(indicatorJudgeRiskFactorEntityPage);
        List<IndicatorJudgeRiskFactorEntity> indicatorJudgeRiskFactorEntityList = indicatorJudgeRiskFactorEntityPage.getRecords();
        List<IndicatorJudgeRiskFactorResponseRs> indicatorJudgeRiskFactorResponseRsList = indicatorJudgeRiskFactorEntityList2ResponseRsList(indicatorJudgeRiskFactorEntityList);
        indicatorJudgeRiskFactorResponseRsPage.setRecords(indicatorJudgeRiskFactorResponseRsList);
        return indicatorJudgeRiskFactorResponseRsPage;
    }

    /**
    * @param
    * @return
    * @说明: 创建危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeRiskFactor(CreateIndicatorJudgeRiskFactorRequest createIndicatorJudgeRiskFactor) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeRiskFactor(String indicatorJudgeRiskFactorId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeRiskFactorRequest updateStatusIndicatorJudgeRiskFactor ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeRiskFactor(UpdateIndicatorJudgeRiskFactorRequest updateIndicatorJudgeRiskFactor ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeRiskFactorResponse getIndicatorJudgeRiskFactor(String indicatorJudgeRiskFactorId ) {
        return new IndicatorJudgeRiskFactorResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeRiskFactorResponse> listIndicatorJudgeRiskFactor(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeRiskFactorResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标危险因素
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeRiskFactor(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}