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
import org.dows.hep.api.exception.IndicatorViewSupportExamException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.hep.service.IndicatorViewSupportExamService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:查看指标辅助检查类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewSupportExamBiz{
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorInstanceService indicatorInstanceService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorViewSupportExamService indicatorViewSupportExamService;
    
    public static IndicatorViewSupportExamResponseRs indicatorViewSupportExam2ResponseRs(
        IndicatorViewSupportExamEntity indicatorViewSupportExamEntity,
        List<IndicatorCategoryResponse> indicatorCategoryResponseList,
        IndicatorInstanceResponseRs indicatorInstanceResponseRs
    ) {
        return IndicatorViewSupportExamResponseRs
            .builder()
            .id(indicatorViewSupportExamEntity.getId())
            .indicatorViewSupportExamId(indicatorViewSupportExamEntity.getIndicatorViewSupportExamId())
            .appId(indicatorViewSupportExamEntity.getAppId())
            .indicatorFuncId(indicatorViewSupportExamEntity.getIndicatorFuncId())
            .name(indicatorViewSupportExamEntity.getName())
            .indicatorCategoryResponseList(indicatorCategoryResponseList)
            .fee(indicatorViewSupportExamEntity.getFee().doubleValue())
            .indicatorInstanceResponseRs(indicatorInstanceResponseRs)
            .resultAnalysis(indicatorViewSupportExamEntity.getResultAnalysis())
            .status(indicatorViewSupportExamEntity.getStatus())
            .dt(indicatorViewSupportExamEntity.getDt())
            .build();
    }

    private List<IndicatorViewSupportExamResponseRs> indicatorViewSupportExamEntityList2ResponseRsList(List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList) {
        if (Objects.isNull(indicatorViewSupportExamEntityList) || indicatorViewSupportExamEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorViewSupportExamEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSetThird = new HashSet<>();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorViewSupportExamEntityList.forEach(
            indicatorViewSupportExamEntity -> {
                indicatorCategoryIdSetThird.add(indicatorViewSupportExamEntity.getIndicatorCategoryId());
                indicatorInstanceIdSet.add(indicatorViewSupportExamEntity.getIndicatorInstanceId());
            });
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdThirdVIndicatorCategoryMap = new HashMap<>();
        Set<String> indicatorCategoryIdSetSecond = new HashSet<>();
        if (!indicatorCategoryIdSetThird.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSetThird)
                .list()
                .forEach(indicatorCategoryEntity -> {
                    indicatorCategoryIdSetSecond.add(indicatorCategoryEntity.getPid());
                    kIndicatorCategoryIdThirdVIndicatorCategoryMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity);
                });
        }
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdSecondVIndicatorCategoryMap = new HashMap<>();
        Set<String> indicatorCategoryIdSetFirst = new HashSet<>();
        if (!indicatorCategoryIdSetSecond.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSetSecond)
                .list()
                .forEach(indicatorCategoryEntity -> {
                    indicatorCategoryIdSetFirst.add(indicatorCategoryEntity.getPid());
                    kIndicatorCategoryIdSecondVIndicatorCategoryMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity);
                });
        }
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdFirstVIndicatorCategoryMap = new HashMap<>();
        if (!indicatorCategoryIdSetFirst.isEmpty()) {
            indicatorCategoryService.lambdaQuery()
                .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSetFirst)
                .list()
                .forEach(indicatorCategoryEntity -> {
                    kIndicatorCategoryIdFirstVIndicatorCategoryMap.put(indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity);
                });
        }
        Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceEntityMap = new HashMap<>();
        if (!indicatorInstanceIdSet.isEmpty()) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                .list()
                .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceEntityMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity));
        }
        return indicatorViewSupportExamEntityList
            .stream()
            .map(indicatorViewSupportExamEntity -> {
                String indicatorCategoryIdThird = indicatorViewSupportExamEntity.getIndicatorCategoryId();
                IndicatorCategoryEntity indicatorCategoryEntityThird = kIndicatorCategoryIdThirdVIndicatorCategoryMap.get(indicatorCategoryIdThird);
                String indicatorCategoryIdSecond = indicatorCategoryEntityThird.getPid();
                IndicatorCategoryEntity indicatorCategoryEntitySecond = kIndicatorCategoryIdSecondVIndicatorCategoryMap.get(indicatorCategoryIdSecond);
                String indicatorCategoryIdFirst = indicatorCategoryEntitySecond.getPid();
                IndicatorCategoryEntity indicatorCategoryEntityFirst= kIndicatorCategoryIdFirstVIndicatorCategoryMap.get(indicatorCategoryIdFirst);
                List<IndicatorCategoryEntity> indicatorCategoryEntityList = new ArrayList<>();
                indicatorCategoryEntityList.add(indicatorCategoryEntityFirst);
                indicatorCategoryEntityList.add(indicatorCategoryEntitySecond);
                indicatorCategoryEntityList.add(indicatorCategoryEntityThird);
                List<IndicatorCategoryResponse> indicatorCategoryResponseList = indicatorCategoryEntityList.stream().map(IndicatorCategoryBiz::indicatorCategoryEntity2Response).collect(Collectors.toList());
                IndicatorInstanceResponseRs indicatorInstanceResponseRs = IndicatorInstanceBiz.indicatorInstance2ResponseRs(
                    kIndicatorInstanceIdVIndicatorInstanceEntityMap.get(indicatorViewSupportExamEntity.getIndicatorInstanceId()),
                    null
                );
                return indicatorViewSupportExam2ResponseRs(
                    indicatorViewSupportExamEntity,
                    indicatorCategoryResponseList,
                    indicatorInstanceResponseRs
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorViewSupportExamRequestRs createOrUpdateIndicatorViewSupportExamRequestRs) {
        IndicatorViewSupportExamEntity indicatorViewSupportExamEntity;
        String appId = createOrUpdateIndicatorViewSupportExamRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorViewSupportExamRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewSupportExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewSupportExamRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorViewSupportExamRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewSupportExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewSupportExamRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorInstanceId = createOrUpdateIndicatorViewSupportExamRequestRs.getIndicatorInstanceId();
        if (StringUtils.isNotBlank(indicatorInstanceId)) {
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewSupportExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewSupportExamRequestRs indicatorInstanceId:{} is illegal", indicatorInstanceId);
                    throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorViewSupportExamId = createOrUpdateIndicatorViewSupportExamRequestRs.getIndicatorViewSupportExamId();
        BigDecimal fee = BigDecimal.valueOf(createOrUpdateIndicatorViewSupportExamRequestRs.getFee());
        if (StringUtils.isBlank(indicatorViewSupportExamId)) {
            indicatorViewSupportExamEntity = IndicatorViewSupportExamEntity
                .builder()
                .indicatorViewSupportExamId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorViewSupportExamRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .fee(fee)
                .indicatorInstanceId(indicatorInstanceId)
                .resultAnalysis(createOrUpdateIndicatorViewSupportExamRequestRs.getResultAnalysis())
                .status(createOrUpdateIndicatorViewSupportExamRequestRs.getStatus())
                .build();
        } else {
            indicatorViewSupportExamEntity = indicatorViewSupportExamService.lambdaQuery()
                .eq(IndicatorViewSupportExamEntity::getAppId, appId)
                .eq(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId, indicatorViewSupportExamId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorViewSupportExamBiz.createOrUpdateRs param createOrUpdateIndicatorViewSupportExamRequestRs indicatorViewSupportExamId:{} is illegal", indicatorViewSupportExamId);
                    throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorViewSupportExamEntity.setName(createOrUpdateIndicatorViewSupportExamRequestRs.getName());
            indicatorViewSupportExamEntity.setStatus(createOrUpdateIndicatorViewSupportExamRequestRs.getStatus());
            indicatorViewSupportExamEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorViewSupportExamEntity.setFee(fee);
            indicatorViewSupportExamEntity.setIndicatorInstanceId(indicatorInstanceId);
            indicatorViewSupportExamEntity.setResultAnalysis(createOrUpdateIndicatorViewSupportExamRequestRs.getResultAnalysis());
        }
        indicatorViewSupportExamService.saveOrUpdate(indicatorViewSupportExamEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorViewSupportExamIdList) {
        if (Objects.isNull(indicatorViewSupportExamIdList) || indicatorViewSupportExamIdList.isEmpty()) {
            log.warn("method IndicatorViewSupportExamBiz.batchDeleteRs param indicatorViewSupportExamIdList is empty");
            throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorViewSupportExamIdSet = indicatorViewSupportExamService.lambdaQuery()
            .in(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId, indicatorViewSupportExamIdList)
            .list()
            .stream()
            .map(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId)
            .collect(Collectors.toSet());
        if (
            indicatorViewSupportExamIdList.stream().anyMatch(indicatorViewSupportExamId -> !dbIndicatorViewSupportExamIdSet.contains(indicatorViewSupportExamId))
        ) {
            log.warn("method IndicatorViewSupportExamBiz.batchDeleteRs param indicatorViewSupportExamIdList:{} is illegal", indicatorViewSupportExamIdList);
            throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = indicatorViewSupportExamService.remove(
            new LambdaQueryWrapper<IndicatorViewSupportExamEntity>()
                .in(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId, dbIndicatorViewSupportExamIdSet)
        );
        if (!isRemove) {
            log.warn("method IndicatorViewSupportExamBiz.batchDeleteRs param indicatorViewSupportExamIdList:{} is illegal", indicatorViewSupportExamIdList);
            throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String indicatorViewSupportExamId, Integer status) {
        IndicatorViewSupportExamEntity indicatorViewSupportExamEntity = indicatorViewSupportExamService.lambdaQuery()
            .eq(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId, indicatorViewSupportExamId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorViewSupportExamBiz.updateStatusRs param indicatorViewSupportExamId:{} is illegal", indicatorViewSupportExamId);
                throw new IndicatorViewSupportExamException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorViewSupportExamEntity.setStatus(status);
        indicatorViewSupportExamService.updateById(indicatorViewSupportExamEntity);
    }

    public IndicatorViewSupportExamResponseRs getRs(String indicatorViewSupportExamId) {
        IndicatorViewSupportExamEntity indicatorViewSupportExamEntity = indicatorViewSupportExamService.lambdaQuery()
            .eq(IndicatorViewSupportExamEntity::getIndicatorViewSupportExamId, indicatorViewSupportExamId)
            .one();
        if (Objects.isNull(indicatorViewSupportExamEntity)) {
            return null;
        }
        List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList = new ArrayList<>();
        indicatorViewSupportExamEntityList.add(indicatorViewSupportExamEntity);
        List<IndicatorViewSupportExamResponseRs> indicatorViewSupportExamResponseRsList = indicatorViewSupportExamEntityList2ResponseRsList(indicatorViewSupportExamEntityList);
        if (indicatorViewSupportExamResponseRsList.isEmpty()) {
            return null;
        }
        return indicatorViewSupportExamResponseRsList.get(0);
    }

    public Page<IndicatorViewSupportExamResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
        Page<IndicatorViewSupportExamEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityLQW = new LambdaQueryWrapper<>();
        indicatorViewSupportExamEntityLQW
            .eq(Objects.nonNull(appId), IndicatorViewSupportExamEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorViewSupportExamEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(Objects.nonNull(status), IndicatorViewSupportExamEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorViewSupportExamEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
        if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
            List<String> paramIndicatorCategoryIdList = Arrays.stream(indicatorCategoryIdList.split(",")).toList();
            indicatorViewSupportExamEntityLQW.in(IndicatorViewSupportExamEntity::getIndicatorCategoryId, paramIndicatorCategoryIdList);
        }
        Page<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityPage = indicatorViewSupportExamService.page(page, indicatorViewSupportExamEntityLQW);
        Page<IndicatorViewSupportExamResponseRs> indicatorViewSupportExamResponseRsPage = RsPageUtil.convertFromAnother(indicatorViewSupportExamEntityPage);
        List<IndicatorViewSupportExamEntity> indicatorViewSupportExamEntityList = indicatorViewSupportExamEntityPage.getRecords();
        List<IndicatorViewSupportExamResponseRs> indicatorViewSupportExamResponseRsList = indicatorViewSupportExamEntityList2ResponseRsList(indicatorViewSupportExamEntityList);
        indicatorViewSupportExamResponseRsPage.setRecords(indicatorViewSupportExamResponseRsList);
        return indicatorViewSupportExamResponseRsPage;
    }

    /**
    * @param
    * @return
    * @说明: 创建查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorViewSupportExam(CreateIndicatorViewSupportExamRequest createIndicatorViewSupportExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorViewSupportExam(String indicatorViewSupportExamId ) {
        
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
    public void updateStatus(IndicatorViewSupportExamRequest indicatorViewSupportExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewSupportExam(UpdateIndicatorViewSupportExamRequest updateIndicatorViewSupportExam ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewSupportExamResponse getIndicatorViewSupportExam(String indicatorViewSupportExamId ) {
        return new IndicatorViewSupportExamResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewSupportExamResponse> listIndicatorViewSupportExam(String appId, String indicatorCategoryId, String name, String type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new ArrayList<IndicatorViewSupportExamResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标辅助检查类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewSupportExam(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest fee, String resultAnalysis, Integer status ) {
        return new String();
    }
}