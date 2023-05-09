package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorViewPhysicalExamResponse;
import org.dows.hep.api.base.indicator.response.IndicatorViewPhysicalExamResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorViewPhysicalExamException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.hep.service.IndicatorViewPhysicalExamService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:查看指标体格检查类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewPhysicalExamBiz{
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorInstanceService indicatorInstanceService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorViewPhysicalExamService indicatorViewPhysicalExamService;

    public static IndicatorViewPhysicalExamResponseRs indicatorViewPhysicalExam2ResponseRs(
        IndicatorViewPhysicalExamEntity indicatorViewPhysicalExamEntity,
        IndicatorCategoryResponse indicatorCategoryResponse,
        IndicatorInstanceResponseRs indicatorInstanceResponseRs
        ) {
        return IndicatorViewPhysicalExamResponseRs
            .builder()
            .id(indicatorViewPhysicalExamEntity.getId())
            .indicatorViewPhysicalExamId(indicatorViewPhysicalExamEntity.getIndicatorViewPhysicalExamId())
            .appId(indicatorViewPhysicalExamEntity.getAppId())
            .indicatorFuncId(indicatorViewPhysicalExamEntity.getIndicatorFuncId())
            .name(indicatorViewPhysicalExamEntity.getName())
            .indicatorCategoryResponse(indicatorCategoryResponse)
            .fee(indicatorViewPhysicalExamEntity.getFee().doubleValue())
            .indicatorInstanceResponseRs(indicatorInstanceResponseRs)
            .resultAnalysis(indicatorViewPhysicalExamEntity.getResultAnalysis())
            .status(indicatorViewPhysicalExamEntity.getStatus())
            .dt(indicatorViewPhysicalExamEntity.getDt())
            .build();
    }

    private List<IndicatorViewPhysicalExamResponseRs> indicatorViewPhysicalExamEntityList2ResponseRsList(
        List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList
    ) {
        if (Objects.isNull(indicatorViewPhysicalExamEntityList) || indicatorViewPhysicalExamEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorViewPhysicalExamEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorViewPhysicalExamEntityList.forEach(
            indicatorViewPhysicalExamEntity -> {
                indicatorCategoryIdSet.add(indicatorViewPhysicalExamEntity.getIndicatorCategoryId());
                indicatorInstanceIdSet.add(indicatorViewPhysicalExamEntity.getIndicatorInstanceId());
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
        Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceEntityMap = new HashMap<>();
        if (!indicatorInstanceIdSet.isEmpty()) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                .list()
                .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceEntityMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity));
        }
        return indicatorViewPhysicalExamEntityList
            .stream()
            .map(indicatorViewPhysicalExamEntity -> {
                IndicatorCategoryResponse indicatorCategoryResponse = kIndicatorCategoryIdVIndicatorCategoryResponseMap.get(indicatorViewPhysicalExamEntity.getIndicatorCategoryId());
                IndicatorInstanceResponseRs indicatorInstanceResponseRs = IndicatorInstanceBiz.indicatorInstance2ResponseRs(
                    kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorViewPhysicalExamEntity.getIndicatorInstanceId())
                );
                return indicatorViewPhysicalExam2ResponseRs(
                    indicatorViewPhysicalExamEntity,
                    indicatorCategoryResponse,
                    indicatorInstanceResponseRs
                );
            })
            .collect(Collectors.toList());
    }

    /**
    * @param
    * @return
    * @说明: 创建查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorViewPhysicalExam(CreateIndicatorViewPhysicalExamRequest createIndicatorViewPhysicalExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorViewPhysicalExam(String indicatorViewPhysicalExamId ) {
        
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
    public void updateStatus(IndicatorViewPhysicalExamRequest indicatorViewPhysicalExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewPhysicalExam(UpdateIndicatorViewPhysicalExamRequest updateIndicatorViewPhysicalExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewPhysicalExamResponse getIndicatorViewPhysicalExam(String indicatorViewPhysicalExamId ) {
        return new IndicatorViewPhysicalExamResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewPhysicalExamResponse> listIndicatorViewPhysicalExam(String appId, String indicatorCategoryId, String name, Integer type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new ArrayList<IndicatorViewPhysicalExamResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选查看指标体格检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewPhysicalExam(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, Integer type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new String();
    }
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorViewPhysicalExamRequestRs createOrUpdateIndicatorViewPhysicalExamRequestRs) {
        IndicatorViewPhysicalExamEntity indicatorViewPhysicalExamEntity;
        String appId = createOrUpdateIndicatorViewPhysicalExamRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorViewPhysicalExamRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewPhysicalExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewPhysicalExamRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorViewPhysicalExamRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewPhysicalExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewPhysicalExamRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorInstanceId = createOrUpdateIndicatorViewPhysicalExamRequestRs.getIndicatorInstanceId();
        if (StringUtils.isNotBlank(indicatorInstanceId)) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewPhysicalExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewPhysicalExamRequestRs indicatorInstanceId:{} is illegal", indicatorInstanceId);
                    throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorViewPhysicalExamId = createOrUpdateIndicatorViewPhysicalExamRequestRs.getIndicatorViewPhysicalExamId();
        BigDecimal fee = BigDecimal.valueOf(createOrUpdateIndicatorViewPhysicalExamRequestRs.getFee());
        if (StringUtils.isBlank(indicatorViewPhysicalExamId)) {
            indicatorViewPhysicalExamEntity = IndicatorViewPhysicalExamEntity
                .builder()
                .indicatorViewPhysicalExamId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorViewPhysicalExamRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .fee(fee)
                .indicatorInstanceId(indicatorInstanceId)
                .resultAnalysis(createOrUpdateIndicatorViewPhysicalExamRequestRs.getResultAnalysis())
                .build();
        } else {
            indicatorViewPhysicalExamEntity = indicatorViewPhysicalExamService.lambdaQuery()
                .eq(IndicatorViewPhysicalExamEntity::getAppId, appId)
                .eq(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId, indicatorViewPhysicalExamId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewPhysicalExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewPhysicalExamRequestRs indicatorViewPhysicalExamId:{} is illegal", indicatorViewPhysicalExamId);
                    throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorViewPhysicalExamEntity.setName(createOrUpdateIndicatorViewPhysicalExamRequestRs.getName());
            indicatorViewPhysicalExamEntity.setStatus(createOrUpdateIndicatorViewPhysicalExamRequestRs.getStatus());
            indicatorViewPhysicalExamEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorViewPhysicalExamEntity.setFee(fee);
            indicatorViewPhysicalExamEntity.setIndicatorInstanceId(indicatorInstanceId);
            indicatorViewPhysicalExamEntity.setResultAnalysis(createOrUpdateIndicatorViewPhysicalExamRequestRs.getResultAnalysis());
        }
        indicatorViewPhysicalExamService.saveOrUpdate(indicatorViewPhysicalExamEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorViewPhysicalExamIdList) {
        if (Objects.isNull(indicatorViewPhysicalExamIdList) || indicatorViewPhysicalExamIdList.isEmpty()) {
            log.warn("method IndicatorViewPhysicalExamBiz.batchDeleteRs param indicatorViewPhysicalExamIdList is empty");
            throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorViewPhysicalExamIdSet = indicatorViewPhysicalExamService.lambdaQuery()
            .in(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId, indicatorViewPhysicalExamIdList)
            .list()
            .stream()
            .map(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId)
            .collect(Collectors.toSet());
        if (
            indicatorViewPhysicalExamIdList.stream().anyMatch(indicatorViewPhysicalExamId -> !dbIndicatorViewPhysicalExamIdSet.contains(indicatorViewPhysicalExamId))
        ) {
            log.warn("method IndicatorViewPhysicalExamBiz.batchDeleteRs param indicatorViewPhysicalExamIdList:{} is illegal", indicatorViewPhysicalExamIdList);
            throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = indicatorViewPhysicalExamService.remove(
            new LambdaQueryWrapper<IndicatorViewPhysicalExamEntity>()
                .in(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId, dbIndicatorViewPhysicalExamIdSet)
        );
        if (!isRemove) {
            log.warn("method IndicatorViewPhysicalExamBiz.batchDeleteRs param indicatorViewPhysicalExamIdList:{} is illegal", indicatorViewPhysicalExamIdList);
            throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String indicatorViewPhysicalExamId, Integer status) {
        IndicatorViewPhysicalExamEntity indicatorViewPhysicalExamEntity = indicatorViewPhysicalExamService.lambdaQuery()
            .eq(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId, indicatorViewPhysicalExamId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorViewPhysicalExamBiz.updateStatusRs param indicatorViewPhysicalExamId:{} is illegal", indicatorViewPhysicalExamId);
                throw new IndicatorViewPhysicalExamException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorViewPhysicalExamEntity.setStatus(status);
        indicatorViewPhysicalExamService.updateById(indicatorViewPhysicalExamEntity);
    }
    public IndicatorViewPhysicalExamResponseRs getRs(String indicatorViewPhysicalExamId) {
        IndicatorViewPhysicalExamEntity indicatorViewPhysicalExamEntity = indicatorViewPhysicalExamService.lambdaQuery()
            .eq(IndicatorViewPhysicalExamEntity::getIndicatorViewPhysicalExamId, indicatorViewPhysicalExamId)
            .one();
        if (Objects.isNull(indicatorViewPhysicalExamEntity)) {
            return null;
        }
        List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList = new ArrayList<>();
        indicatorViewPhysicalExamEntityList.add(indicatorViewPhysicalExamEntity);
        List<IndicatorViewPhysicalExamResponseRs> indicatorViewPhysicalExamResponseRsList = indicatorViewPhysicalExamEntityList2ResponseRsList(indicatorViewPhysicalExamEntityList);
        return indicatorViewPhysicalExamResponseRsList.get(0);
    }

    public IPage<IndicatorViewPhysicalExamResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String paramIndicatorCategoryId, Integer status) {
        Page<IndicatorViewPhysicalExamEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityLQW = new LambdaQueryWrapper<>();
        indicatorViewPhysicalExamEntityLQW
            .eq(Objects.nonNull(appId), IndicatorViewPhysicalExamEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorViewPhysicalExamEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(StringUtils.isNotBlank(paramIndicatorCategoryId), IndicatorViewPhysicalExamEntity::getIndicatorCategoryId, paramIndicatorCategoryId)
            .eq(Objects.nonNull(status), IndicatorViewPhysicalExamEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorViewPhysicalExamEntity::getName, StringUtils.isNotBlank(name) ? null : name.trim());
        Page<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityPage = indicatorViewPhysicalExamService.page(page, indicatorViewPhysicalExamEntityLQW);
        Page<IndicatorViewPhysicalExamResponseRs> indicatorViewPhysicalExamResponseRsPage = RsPageUtil.convertFromAnother(indicatorViewPhysicalExamEntityPage);
        List<IndicatorViewPhysicalExamEntity> indicatorViewPhysicalExamEntityList = indicatorViewPhysicalExamEntityPage.getRecords();
        List<IndicatorViewPhysicalExamResponseRs> indicatorViewPhysicalExamResponseRsList = indicatorViewPhysicalExamEntityList2ResponseRsList(indicatorViewPhysicalExamEntityList);
        indicatorViewPhysicalExamResponseRsPage.setRecords(indicatorViewPhysicalExamResponseRsList);
        return indicatorViewPhysicalExamResponseRsPage;
    }
}