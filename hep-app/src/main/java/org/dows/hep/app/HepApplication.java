package org.dows.hep.app;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.sequence.api.IdGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        SpringApplication.run(HepApplication.class, args);
    }
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;

    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdVIndicatorCategoryMap = new HashMap<>();
        indicatorCategoryService.list()
            .forEach(indicatorCategoryEntity -> kIndicatorCategoryIdVIndicatorCategoryMap.put(
                indicatorCategoryEntity.getIndicatorCategoryId(), indicatorCategoryEntity));
        List<IndicatorCategoryEntity> indicatorCategoryEntityList = new ArrayList<>();
        for (EnumIndicatorCategory enumIndicatorCategory : EnumIndicatorCategory.values()) {
            String indicatorCategoryId = enumIndicatorCategory.getCode();
            String categoryName = enumIndicatorCategory.getCategoryName();
            IndicatorCategoryEntity indicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryMap.get(indicatorCategoryId);
            if (Objects.isNull(indicatorCategoryEntity)) {
                indicatorCategoryEntityList.add(IndicatorCategoryEntity
                    .builder()
                    .indicatorCategoryId(indicatorCategoryId)
                    .appId("3")
                    .pid(null)
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
    }
}

