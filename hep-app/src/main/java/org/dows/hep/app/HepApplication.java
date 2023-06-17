package org.dows.hep.app;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.base.indicator.IndicatorFuncBiz;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.sequence.api.IdGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* @description
*
* @author 
* @date 2023年4月14日 下午3:45:06
*/
@SpringBootApplication(scanBasePackages = {"org.dows.hep.*",
                                           "org.dows.framework.*",
                                           "org.dows.account.*",
                                           "org.dows.user.*",
                                           "org.dows.rbac.*"})
@MapperScan(basePackages = {"org.dows.*.mapper"})
@RequiredArgsConstructor
public class HepApplication{
    public static void main(String[] args) {
        System.setProperty("SERVICE_NAME","hep");
        SpringApplication.run(HepApplication.class, args);
    }
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;

    private final IndicatorFuncBiz indicatorFuncBiz;

    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void init() throws InterruptedException {
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdVIndicatorCategoryMap = new HashMap<>();
        indicatorCategoryService.list()
            .forEach(indicatorCategoryEntity -> kIndicatorCategoryIdVIndicatorCategoryMap.put(
                indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity));
        List<IndicatorCategoryEntity> indicatorCategoryEntityList = new ArrayList<>();
        for (EnumIndicatorCategory enumIndicatorCategory : EnumIndicatorCategory.values()) {
            String indicatorCategoryId = enumIndicatorCategory.getCode();
            String pid = indicatorCategoryId.length() <= 3 ? null : indicatorCategoryId.substring(0, indicatorCategoryId.length() - 2);
            String categoryName = enumIndicatorCategory.getCategoryName();
            IndicatorCategoryEntity indicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryMap.get(indicatorCategoryId);
            if (Objects.isNull(indicatorCategoryEntity)) {
                indicatorCategoryEntityList.add(IndicatorCategoryEntity
                    .builder()
                    .indicatorCategoryId(indicatorCategoryId)
                    .appId("3")
                    .pid(pid)
                    .categoryName(categoryName)
                    .build()
                );
            } else {
                indicatorCategoryEntity.setIndicatorCategoryId(indicatorCategoryId);
                indicatorCategoryEntity.setCategoryName(categoryName);
                indicatorCategoryEntityList.add(indicatorCategoryEntity);
            }
        }
        indicatorCategoryService.saveOrUpdateBatch(indicatorCategoryEntityList);
        List<String> indicatorCategoryIdList = indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getAppId, EnumString.APP_ID.getStr())
            .eq(IndicatorFuncEntity::getPid, EnumIndicatorCategory.OPERATE_MANAGEMENT.getCode())
            .list()
            .stream()
            .map(IndicatorFuncEntity::getIndicatorCategoryId)
            .collect(Collectors.toList());
        if (!indicatorCategoryIdList.contains(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_DIET.getCode())) {
            indicatorFuncBiz.create(CreateIndicatorFuncRequest
                .builder()
                .appId(EnumString.APP_ID.getStr())
                .pid(EnumIndicatorCategory.OPERATE_MANAGEMENT.getCode())
                .indicatorCategoryId(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_DIET.getCode())
                .name(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_DIET.getCategoryName())
                .build());
        }
        if (!indicatorCategoryIdList.contains(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_SPORTS.getCode())) {
            indicatorFuncBiz.create(CreateIndicatorFuncRequest
                .builder()
                .appId(EnumString.APP_ID.getStr())
                .pid(EnumIndicatorCategory.OPERATE_MANAGEMENT.getCode())
                .indicatorCategoryId(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_SPORTS.getCode())
                .name(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_SPORTS.getCategoryName())
                .build());
        }
    }
}

