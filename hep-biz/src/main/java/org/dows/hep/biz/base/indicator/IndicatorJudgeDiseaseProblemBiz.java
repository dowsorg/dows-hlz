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
import org.dows.hep.api.exception.IndicatorJudgeDiseaseProblemException;
import org.dows.hep.biz.util.RsPageUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorJudgeDiseaseProblemService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:指标:判断指标疾病问题
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorJudgeDiseaseProblemBiz {
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorJudgeDiseaseProblemService indicatorJudgeDiseaseProblemService;

    public static IndicatorJudgeDiseaseProblemResponseRs indicatorJudgeDiseaseProblem2ResponseRs(
        IndicatorJudgeDiseaseProblemEntity indicatorJudgeDiseaseProblemEntity,
        List<IndicatorCategoryResponse> indicatorCategoryResponseList
    ) {
        if (Objects.isNull(indicatorJudgeDiseaseProblemEntity)) {
            return null;
        }
        return IndicatorJudgeDiseaseProblemResponseRs
            .builder()
            .id(indicatorJudgeDiseaseProblemEntity.getId())
            .indicatorJudgeDiseaseProblemId(indicatorJudgeDiseaseProblemEntity.getIndicatorJudgeDiseaseProblemId())
            .appId(indicatorJudgeDiseaseProblemEntity.getAppId())
            .indicatorFuncId(indicatorJudgeDiseaseProblemEntity.getIndicatorFuncId())
            .name(indicatorJudgeDiseaseProblemEntity.getName())
            .indicatorCategoryResponseList(indicatorCategoryResponseList)
            .point(indicatorJudgeDiseaseProblemEntity.getPoint().doubleValue())
            .expression(indicatorJudgeDiseaseProblemEntity.getExpression())
            .resultExplain(indicatorJudgeDiseaseProblemEntity.getResultExplain())
            .status(indicatorJudgeDiseaseProblemEntity.getStatus())
            .dt(indicatorJudgeDiseaseProblemEntity.getDt())
            .build();
    }

    private List<IndicatorJudgeDiseaseProblemResponseRs> indicatorJudgeDiseaseProblemEntityList2ResponseRsList(List<IndicatorJudgeDiseaseProblemEntity> indicatorJudgeDiseaseProblemEntityList) {
        if (Objects.isNull(indicatorJudgeDiseaseProblemEntityList) || indicatorJudgeDiseaseProblemEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        String appId = indicatorJudgeDiseaseProblemEntityList.get(0).getAppId();
        Set<String> indicatorCategoryIdSetThird = new HashSet<>();
        indicatorJudgeDiseaseProblemEntityList.forEach(
            indicatorJudgeDiseaseProblemEntity -> {
                indicatorCategoryIdSetThird.add(indicatorJudgeDiseaseProblemEntity.getIndicatorCategoryId());
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
        return indicatorJudgeDiseaseProblemEntityList
            .stream()
            .map(indicatorJudgeDiseaseProblemEntity -> {
                String indicatorCategoryIdThird = indicatorJudgeDiseaseProblemEntity.getIndicatorCategoryId();
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
                return indicatorJudgeDiseaseProblem2ResponseRs(
                    indicatorJudgeDiseaseProblemEntity,
                    indicatorCategoryResponseList
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorJudgeDiseaseProblemRequestRs createOrUpdateIndicatorJudgeDiseaseProblemRequestRs) {
        IndicatorJudgeDiseaseProblemEntity indicatorJudgeDiseaseProblemEntity;
        String appId = createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getIndicatorFuncId();
        if (StringUtils.isNotBlank(indicatorFuncId)) {
            indicatorFuncService.lambdaQuery()
                .eq(IndicatorFuncEntity::getAppId, appId)
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeDiseaseProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeDiseaseProblemRequestRs indicatorFuncId:{} is illegal", indicatorFuncId);
                    throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorCategoryId = createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getIndicatorCategoryId();
        if (StringUtils.isNotBlank(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeDiseaseProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeDiseaseProblemRequestRs indicatorCategoryId:{} is illegal", indicatorCategoryId);
                    throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String indicatorJudgeDiseaseProblemId = createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getIndicatorJudgeDiseaseProblemId();
        BigDecimal point = BigDecimal.valueOf(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getPoint());
        if (StringUtils.isBlank(indicatorJudgeDiseaseProblemId)) {
            indicatorJudgeDiseaseProblemEntity = IndicatorJudgeDiseaseProblemEntity
                .builder()
                .indicatorJudgeDiseaseProblemId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorFuncId(indicatorFuncId)
                .name(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getName())
                .indicatorCategoryId(indicatorCategoryId)
                .point(point)
                .resultExplain(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getResultExplain())
                .status(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getStatus())
                .build();
        } else {
            indicatorJudgeDiseaseProblemEntity = indicatorJudgeDiseaseProblemService.lambdaQuery()
                .eq(IndicatorJudgeDiseaseProblemEntity::getAppId, appId)
                .eq(IndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId, indicatorJudgeDiseaseProblemId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method IndicatorJudgeDiseaseProblemBiz.createOrUpdateRs param createOrUpdateIndicatorJudgeDiseaseProblemRequestRs indicatorJudgeDiseaseProblemId:{} is illegal", indicatorJudgeDiseaseProblemId);
                    throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorJudgeDiseaseProblemEntity.setName(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getName());
            indicatorJudgeDiseaseProblemEntity.setStatus(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getStatus());
            indicatorJudgeDiseaseProblemEntity.setIndicatorCategoryId(indicatorCategoryId);
            indicatorJudgeDiseaseProblemEntity.setPoint(point);
            indicatorJudgeDiseaseProblemEntity.setExpression(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getExpression());
            indicatorJudgeDiseaseProblemEntity.setResultExplain(createOrUpdateIndicatorJudgeDiseaseProblemRequestRs.getResultExplain());
        }
        indicatorJudgeDiseaseProblemService.saveOrUpdate(indicatorJudgeDiseaseProblemEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRs(List<String> indicatorJudgeDiseaseProblemIdList) {
        if (Objects.isNull(indicatorJudgeDiseaseProblemIdList) || indicatorJudgeDiseaseProblemIdList.isEmpty()) {
            log.warn("method IndicatorJudgeDiseaseProblemBiz.batchDeleteRs param indicatorJudgeDiseaseProblemIdList is empty");
            throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
        }
        Set<String> dbIndicatorJudgeDiseaseProblemIdSet = indicatorJudgeDiseaseProblemService.lambdaQuery()
            .in(IndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId, indicatorJudgeDiseaseProblemIdList)
            .list()
            .stream()
            .map(IndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId)
            .collect(Collectors.toSet());
        if (
            indicatorJudgeDiseaseProblemIdList.stream().anyMatch(indicatorJudgeDiseaseProblemId -> !dbIndicatorJudgeDiseaseProblemIdSet.contains(indicatorJudgeDiseaseProblemId))
        ) {
            log.warn("method IndicatorJudgeDiseaseProblemBiz.batchDeleteRs param indicatorJudgeDiseaseProblemIdList:{} is illegal", indicatorJudgeDiseaseProblemIdList);
            throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemove = indicatorJudgeDiseaseProblemService.remove(
            new LambdaQueryWrapper<IndicatorJudgeDiseaseProblemEntity>()
                .in(IndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId, dbIndicatorJudgeDiseaseProblemIdSet)
        );
        if (!isRemove) {
            log.warn("method IndicatorJudgeDiseaseProblemBiz.batchDeleteRs param indicatorJudgeDiseaseProblemIdList:{} is illegal", indicatorJudgeDiseaseProblemIdList);
            throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusRs(String indicatorJudgeDiseaseProblemId, Integer status) {
        IndicatorJudgeDiseaseProblemEntity indicatorJudgeDiseaseProblemEntity = indicatorJudgeDiseaseProblemService.lambdaQuery()
            .eq(IndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId, indicatorJudgeDiseaseProblemId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorJudgeDiseaseProblemBiz.updateStatusRs param indicatorJudgeDiseaseProblemId:{} is illegal", indicatorJudgeDiseaseProblemId);
                throw new IndicatorJudgeDiseaseProblemException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorJudgeDiseaseProblemEntity.setStatus(status);
        indicatorJudgeDiseaseProblemService.updateById(indicatorJudgeDiseaseProblemEntity);
    }

    public IndicatorJudgeDiseaseProblemResponseRs getRs(String indicatorJudgeDiseaseProblemId) {
        IndicatorJudgeDiseaseProblemEntity indicatorJudgeDiseaseProblemEntity = indicatorJudgeDiseaseProblemService.lambdaQuery()
            .eq(IndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId, indicatorJudgeDiseaseProblemId)
            .one();
        if (Objects.isNull(indicatorJudgeDiseaseProblemEntity)) {
            return null;
        }
        List<IndicatorJudgeDiseaseProblemEntity> indicatorJudgeDiseaseProblemEntityList = new ArrayList<>();
        indicatorJudgeDiseaseProblemEntityList.add(indicatorJudgeDiseaseProblemEntity);
        List<IndicatorJudgeDiseaseProblemResponseRs> indicatorJudgeDiseaseProblemResponseRsList = indicatorJudgeDiseaseProblemEntityList2ResponseRsList(indicatorJudgeDiseaseProblemEntityList);
        if (indicatorJudgeDiseaseProblemResponseRsList.isEmpty()) {
            return null;
        }
        return indicatorJudgeDiseaseProblemResponseRsList.get(0);
    }

    public Page<IndicatorJudgeDiseaseProblemResponseRs> pageRs(Long pageNo, Long pageSize, String order, Boolean asc, String appId, String indicatorFuncId, String name, String indicatorCategoryIdList, Integer status) {
        Page<IndicatorJudgeDiseaseProblemEntity> page = RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
        LambdaQueryWrapper<IndicatorJudgeDiseaseProblemEntity> indicatorJudgeDiseaseProblemEntityLQW = new LambdaQueryWrapper<>();
        indicatorJudgeDiseaseProblemEntityLQW
            .eq(Objects.nonNull(appId), IndicatorJudgeDiseaseProblemEntity::getAppId, appId)
            .eq(StringUtils.isNotBlank(indicatorFuncId), IndicatorJudgeDiseaseProblemEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(Objects.nonNull(status), IndicatorJudgeDiseaseProblemEntity::getStatus, status)
            .like(StringUtils.isNotBlank(name), IndicatorJudgeDiseaseProblemEntity::getName, StringUtils.isBlank(name) ? null : name.trim());
        if (StringUtils.isNotBlank(indicatorCategoryIdList)) {
            Set<String> firstIndicatorCategoryIdSet = Arrays.stream(indicatorCategoryIdList.split(",")).collect(Collectors.toSet());
            Set<String> thirdIndicatorCategoryIdSet = getThirdIndicatorCategoryIdSet(firstIndicatorCategoryIdSet);
            /* runsix:if first category list mapped third category list is empty, means nothing */
            if (thirdIndicatorCategoryIdSet.isEmpty()) {
                return RsPageUtil.getRsPage(pageNo, pageSize, order, asc);
            } else {
                indicatorJudgeDiseaseProblemEntityLQW.in(IndicatorJudgeDiseaseProblemEntity::getIndicatorCategoryId, thirdIndicatorCategoryIdSet);
            }
        }
        Page<IndicatorJudgeDiseaseProblemEntity> indicatorJudgeDiseaseProblemEntityPage = indicatorJudgeDiseaseProblemService.page(page, indicatorJudgeDiseaseProblemEntityLQW);
        Page<IndicatorJudgeDiseaseProblemResponseRs> indicatorJudgeDiseaseProblemResponseRsPage = RsPageUtil.convertFromAnother(indicatorJudgeDiseaseProblemEntityPage);
        List<IndicatorJudgeDiseaseProblemEntity> indicatorJudgeDiseaseProblemEntityList = indicatorJudgeDiseaseProblemEntityPage.getRecords();
        List<IndicatorJudgeDiseaseProblemResponseRs> indicatorJudgeDiseaseProblemResponseRsList = indicatorJudgeDiseaseProblemEntityList2ResponseRsList(indicatorJudgeDiseaseProblemEntityList);
        indicatorJudgeDiseaseProblemResponseRsPage.setRecords(indicatorJudgeDiseaseProblemResponseRsList);
        return indicatorJudgeDiseaseProblemResponseRsPage;
    }

    private Set<String> getThirdIndicatorCategoryIdSet(Collection<String> firstIndicatorCategoryIdCollection) {
        Set<String> resultSet = new HashSet<>();
        if (Objects.nonNull(firstIndicatorCategoryIdCollection) && !firstIndicatorCategoryIdCollection.isEmpty()) {
            Set<String> secondIndicatorCategoryIdSet = new HashSet<>();
            indicatorCategoryService.lambdaQuery()
                .in(IndicatorCategoryEntity::getPid, firstIndicatorCategoryIdCollection)
                .list()
                .forEach(indicatorCategoryEntity -> {
                    secondIndicatorCategoryIdSet.add(indicatorCategoryEntity.getIndicatorCategoryId());
                });
            if (!secondIndicatorCategoryIdSet.isEmpty()) {
                indicatorCategoryService.lambdaQuery()
                    .in(IndicatorCategoryEntity::getPid, secondIndicatorCategoryIdSet)
                    .list()
                    .forEach(indicatorCategoryEntity -> {
                        resultSet.add(indicatorCategoryEntity.getIndicatorCategoryId());
                    });
            }
        }
        return resultSet;
    }
    
    /**
    * @param
    * @return
    * @说明: 创建判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorJudgeDiseaseProblem(CreateIndicatorJudgeDiseaseProblemRequest createIndicatorJudgeDiseaseProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorJudgeDiseaseProblem(String indicatorJudgeDiseaseProblemId ) {
        
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
    public void updateStatus(UpdateStatusIndicatorJudgeDiseaseProblemRequest updateStatusIndicatorJudgeDiseaseProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorJudgeDiseaseProblem(UpdateIndicatorJudgeDiseaseProblemRequest updateIndicatorJudgeDiseaseProblem ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorJudgeDiseaseProblemResponse getIndicatorJudgeDiseaseProblem(String indicatorJudgeDiseaseProblemId ) {
        return new IndicatorJudgeDiseaseProblemResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorJudgeDiseaseProblemResponse> listIndicatorJudgeDiseaseProblem(String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new ArrayList<IndicatorJudgeDiseaseProblemResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选判断指标疾病问题
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorJudgeDiseaseProblem(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name, String type, DecimalRequest point, String expression, String resultExplain, Integer status ) {
        return new String();
    }
}