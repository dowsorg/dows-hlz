package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorJudgeHealthProblemException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.entity.IndicatorJudgeHealthProblemEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorJudgeHealthProblemService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeHealthProblemBiz{
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;

    public static IndicatorJudgeHealthProblemResponseRs indicatorJudgeHealthProblem2ResponseRs(
        IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity,
        List<IndicatorCategoryResponse> indicatorCategoryResponseList
    ) {
        return IndicatorJudgeHealthProblemResponseRs
            .builder()
            .id(indicatorJudgeHealthProblemEntity.getId())
            .indicatorJudgeHealthProblemId(indicatorJudgeHealthProblemEntity.getIndicatorJudgeHealthProblemId())
            .appId(indicatorJudgeHealthProblemEntity.getAppId())
            .indicatorFuncId(indicatorJudgeHealthProblemEntity.getIndicatorFuncId())
            .name(indicatorJudgeHealthProblemEntity.getName())
            .indicatorCategoryResponseList(indicatorCategoryResponseList)
            .point(indicatorJudgeHealthProblemEntity.getPoint().doubleValue())
            .expression(indicatorJudgeHealthProblemEntity.getExpression())
            .resultExplain(indicatorJudgeHealthProblemEntity.getResultExplain())
            .status(indicatorJudgeHealthProblemEntity.getStatus())
            .dt(indicatorJudgeHealthProblemEntity.getDt())
            .build();
    }

    private List<IndicatorJudgeHealthProblemResponseRs> indicatorJudgeHealthProblemEntityList2ResponseRsList(
        List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList
    ) {
        if (Objects.isNull(indicatorJudgeHealthProblemEntityList) || indicatorJudgeHealthProblemEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeHealthProblemEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSetSecond = new HashSet<>();
        indicatorJudgeHealthProblemEntityList.forEach(
            indicatorJudgeHealthProblemEntity -> {
                indicatorCategoryIdSetSecond.add(indicatorJudgeHealthProblemEntity.getIndicatorCategoryId());
            });
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
        return indicatorJudgeHealthProblemEntityList
            .stream()
            .map(indicatorJudgeHealthProblemEntity -> {
                List<IndicatorCategoryResponse> indicatorCategoryResponseList = new ArrayList<>();
                String indicatorCategoryIdSecond = indicatorJudgeHealthProblemEntity.getIndicatorCategoryId();
                IndicatorCategoryEntity indicatorCategoryEntitySecond = kIndicatorCategoryIdSecondVIndicatorCategoryMap.get(indicatorCategoryIdSecond);
                if (Objects.nonNull(indicatorCategoryEntitySecond)) {
                    List<IndicatorCategoryEntity> indicatorCategoryEntityList = new ArrayList<>();
                    String indicatorCategoryIdFirst = indicatorCategoryEntitySecond.getPid();
                    IndicatorCategoryEntity indicatorCategoryEntityFirst= kIndicatorCategoryIdFirstVIndicatorCategoryMap.get(indicatorCategoryIdFirst);
                    if (Objects.nonNull(indicatorCategoryEntityFirst)) {
                        indicatorCategoryEntityList.add(indicatorCategoryEntityFirst);
                    }
                    indicatorCategoryEntityList.add(indicatorCategoryEntitySecond);
                    indicatorCategoryResponseList = indicatorCategoryEntityList.stream().map(IndicatorCategoryBiz::indicatorCategoryEntity2Response).collect(Collectors.toList());
                }
                return indicatorJudgeHealthProblem2ResponseRs(
                    indicatorJudgeHealthProblemEntity,
                    indicatorCategoryResponseList
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorJudgeHealthProblemRequestRs createOrUpdateIndicatorJudgeHealthProblemRequestRs) {
        IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity;
        String appId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthProblemRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthProblemRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorJudgeHealthProblemId = createOrUpdateIndicatorJudgeHealthProblemRequestRs.getIndicatorJudgeHealthProblemId();
        BigDecimal point = BigDecimal.valueOf(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getPoint());
        if (StringUtils.isBlank(indicatorJudgeHealthProblemId)) {
            indicatorJudgeHealthProblemEntity = IndicatorJudgeHealthProblemEntity
                .builder()
                .indicatorJudgeHealthProblemId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .point(point)
                .resultExplain(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getResultExplain())
                .build();
        } else {
            indicatorJudgeHealthProblemEntity = indicatorJudgeHealthProblemService.lambdaQuery()
                .eq(IndicatorJudgeHealthProblemEntity::getAppId, appId)
                .eq(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeHealthProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeHealthProblemRequestRs indicatorJudgeHealthProblemId:{} is illegal", indicatorJudgeHealthProblemId);
                    throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorJudgeHealthProblemEntity.setName(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getName());
            indicatorJudgeHealthProblemEntity.setStatus(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getStatus());
            indicatorJudgeHealthProblemEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorJudgeHealthProblemEntity.setPoint(point);
            indicatorJudgeHealthProblemEntity.setExpression(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getExpression());
            indicatorJudgeHealthProblemEntity.setResultExplain(createOrUpdateIndicatorJudgeHealthProblemRequestRs.getResultExplain());
        }
        indicatorJudgeHealthProblemService.saveOrUpdate(indicatorJudgeHealthProblemEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorJudgeHealthProblemIdList) {
        if (Objects.isNull(indicatorJudgeHealthProblemIdList) || indicatorJudgeHealthProblemIdList.isEmpty()) {
            log.warn("method IndicatorJudgeHealthProblemBiz.batchDeleteRs param indicatorJudgeHealthProblemIdList is empty");
            throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorJudgeHealthProblemIdSet = indicatorJudgeHealthProblemService.lambdaQuery()
            .in(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemIdList)
            .list()
            .stream()
            .map(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId)
            .collect(Collectors.toSet());
        if (
            indicatorJudgeHealthProblemIdList.stream().anyMatch(indicatorJudgeHealthProblemId -> !dbIndicatorJudgeHealthProblemIdSet.contains(indicatorJudgeHealthProblemId))
        ) {
            log.warn("method IndicatorJudgeHealthProblemBiz.batchDeleteRs param indicatorJudgeHealthProblemIdList:{} is illegal", indicatorJudgeHealthProblemIdList);
            throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = indicatorJudgeHealthProblemService.remove(
            new LambdaQueryWrapper<IndicatorJudgeHealthProblemEntity>()
                .in(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, dbIndicatorJudgeHealthProblemIdSet)
        );
        if (!isRemove) {
            log.warn("method IndicatorJudgeHealthProblemBiz.batchDeleteRs param indicatorJudgeHealthProblemIdList:{} is illegal", indicatorJudgeHealthProblemIdList);
            throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String indicatorJudgeHealthProblemId, Integer status) {
        IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity = indicatorJudgeHealthProblemService.lambdaQuery()
            .eq(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorJudgeHealthProblemBiz.updateStatusRs param indicatorJudgeHealthProblemId:{} is illegal", indicatorJudgeHealthProblemId);
                throw new IndicatorJudgeHealthProblemException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorJudgeHealthProblemEntity.setStatus(status);
        indicatorJudgeHealthProblemService.updateById(indicatorJudgeHealthProblemEntity);
    }

    public IndicatorJudgeHealthProblemResponseRs getRs(String indicatorJudgeHealthProblemId) {
        IndicatorJudgeHealthProblemEntity indicatorJudgeHealthProblemEntity = indicatorJudgeHealthProblemService.lambdaQuery()
            .eq(IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemId)
            .one();
        if (Objects.isNull(indicatorJudgeHealthProblemEntity)) {
            return null;
        }
        List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList = new ArrayList<>();
        indicatorJudgeHealthProblemEntityList.add(indicatorJudgeHealthProblemEntity);
        List<IndicatorJudgeHealthProblemResponseRs> indicatorJudgeHealthProblemResponseRsList = indicatorJudgeHealthProblemEntityList2ResponseRsList(indicatorJudgeHealthProblemEntityList);
        if (indicatorJudgeHealthProblemResponseRsList.isEmpty()) {
            return null;
        }
        return indicatorJudgeHealthProblemResponseRsList.get(0);
    }

    public Page<IndicatorJudgeHealthProblemResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
        Page<IndicatorJudgeHealthProblemEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityLQW = new LambdaQueryWrapper<>();
        indicatorJudgeHealthProblemEntityLQW
            .eq(Objects.nonNull(appId), IndicatorJudgeHealthProblemEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorJudgeHealthProblemEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(Objects.nonNull(status), IndicatorJudgeHealthProblemEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorJudgeHealthProblemEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
        if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
            List<String> paramIndicatorCategoryIdList = Arrays.stream(indicatorCategoryIdList.split(",")).toList();
            indicatorJudgeHealthProblemEntityLQW.in(IndicatorJudgeHealthProblemEntity::getIndicatorCategoryId, paramIndicatorCategoryIdList);
        }
        Page<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityPage = indicatorJudgeHealthProblemService.page(page, indicatorJudgeHealthProblemEntityLQW);
        Page<IndicatorJudgeHealthProblemResponseRs> indicatorJudgeHealthProblemResponseRsPage = RsPageUtil.convertFromAnother(indicatorJudgeHealthProblemEntityPage);
        List<IndicatorJudgeHealthProblemEntity> indicatorJudgeHealthProblemEntityList = indicatorJudgeHealthProblemEntityPage.getRecords();
        List<IndicatorJudgeHealthProblemResponseRs> indicatorJudgeHealthProblemResponseRsList = indicatorJudgeHealthProblemEntityList2ResponseRsList(indicatorJudgeHealthProblemEntityList);
        indicatorJudgeHealthProblemResponseRsPage.setRecords(indicatorJudgeHealthProblemResponseRsList);
        return indicatorJudgeHealthProblemResponseRsPage;
    }
    
    /**
    * @param
    * @return
    * @说明: 创建判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeHealthProblem(CreateIndicatorJudgeHealthProblemRequest createIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeHealthProblem(String indicatorJudgeHealthProblemId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeHealthProblemRequest updateStatusIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeHealthProblem(UpdateIndicatorJudgeHealthProblemRequest updateIndicatorJudgeHealthProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeHealthProblemResponse getIndicatorJudgeHealthProblem(String indicatorJudgeHealthProblemId ) {
        return new IndicatorJudgeHealthProblemResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeHealthProblemResponse> listIndicatorJudgeHealthProblem(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeHealthProblemResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标健康问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeHealthProblem(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}