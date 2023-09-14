package org.dows.hep.app;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorInstanceRequestRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.biz.base.indicator.IndicatorFuncBiz;
import org.dows.hep.biz.base.indicator.IndicatorInstanceBiz;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.hep.websocket.HepClientMonitor;
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
                                           "org.dows.edw.*",
                                           "org.dows.framework.*",
                                           "org.dows.account.*",
                                           "org.dows.user.*",
                                           "org.dows.rbac.*"})
@MapperScan(basePackages = {"org.dows.*.mapper"})
@RequiredArgsConstructor
@Slf4j
public class HepApplication{
    public static void main(String[] args) {
        //TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        System.setProperty("SERVICE_NAME","hep");
        SpringApplication.run(HepApplication.class, args);
    }
    private final IdGenerator idGenerator;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorFuncBiz indicatorFuncBiz;
    private final IndicatorInstanceService indicatorInstanceService;
    private final IndicatorInstanceBiz indicatorInstanceBiz;

    private final HepClientMonitor hepClientMonitor;


    @PreDestroy
    public void shutdownApp() {
        log.info("应用关闭！");
        hepClientMonitor.shutdown();
    }
    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void init() throws InterruptedException {
        /* runsix:init IndicatorCategory */
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
        /* runsix:init MUST indicator */
        Set<Integer> allMustType = EnumIndicatorType.kTypeVEnumIndicatorTypeMap.keySet();
        allMustType.remove(EnumIndicatorType.USER_CREATED.getType());
        Set<Integer> dbExistMustType = new HashSet<>();
        Set<EnumIndicatorType> needInitMustType = new HashSet<>();
        indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getAppId, EnumString.APP_ID.getStr())
                .and(i->i.ne(IndicatorInstanceEntity::getType, EnumIndicatorType.USER_CREATED.getType())
                .or()
                .in(IndicatorInstanceEntity::getIndicatorName,EnumIndicatorType.SPORT_ENERGY.getDesc()))

            .list()
            .forEach(indicatorInstanceEntity -> {
                dbExistMustType.add(indicatorInstanceEntity.getType());
            });
        allMustType.forEach(mustType -> {
            if (!dbExistMustType.contains(mustType)) {
                needInitMustType.add(EnumIndicatorType.kTypeVEnumIndicatorTypeMap.get(mustType));
            }
        });
        List<CreateOrUpdateIndicatorInstanceRequestRs> createOrUpdateIndicatorInstanceRequestRsList = new ArrayList<>();
        /* runsix:这里可控，并且只有第一次，所以使用了for insert。原则上用户调用的必须使用批量，不允许for数据库操作 */
        for (EnumIndicatorType enumIndicatorType:needInitMustType) {
            switch (enumIndicatorType) {
                case MONEY -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.INDICATOR_MANAGEMENT_MONEY.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.MONEY.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("20000")
                    .unit("元")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.MONEY.getType())
                    .min("0")
                    .max(String.valueOf(Integer.MAX_VALUE))
                    .build());
                case SEX -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.INDICATOR_MANAGEMENT_BASE_INFO.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.SEX.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("男")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.SEX.getType())
                    .build());
                case HEIGHT -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.INDICATOR_MANAGEMENT_BASE_INFO.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.HEIGHT.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("180")
                    .unit("cm")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.HEIGHT.getType())
                    .min("0")
                    .max("300")
                    .build());
                case WEIGHT -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.INDICATOR_MANAGEMENT_BASE_INFO.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.WEIGHT.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("70")
                    .unit("kg")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.WEIGHT.getType())
                    .min("0")
                    .max("500")
                    .build());
                case HEALTH_POINT -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.INDICATOR_MANAGEMENT_HEALTH.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.HEALTH_POINT.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("50")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.HEALTH_POINT.getType())
                    .min("1")
                    .max("100")
                    .build());
                case AGE -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.INDICATOR_MANAGEMENT_BASE_INFO.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.AGE.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("30")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.AGE.getType())
                    .min("1")
                    .max("200")
                    .build());
                case DURATION -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                    .builder()
                    .indicatorCategoryId(EnumIndicatorCategory.SYSTEM_CALCULATE_INDICATOR.getCode())
                    .appId(EnumString.APP_ID.getStr())
                    .indicatorName(EnumIndicatorType.DURATION.getDesc())
                    .displayByPercent(EnumStatus.DISABLE.getCode())
                    .def("1")
                    .core(EnumStatus.ENABLE.getCode())
                    .food(EnumStatus.DISABLE.getCode())
                    .type(EnumIndicatorType.DURATION.getType())
                    .min("0")
                    .max("10000")
                    .build());
                case SPORT_ENERGY -> createOrUpdateIndicatorInstanceRequestRsList.add(CreateOrUpdateIndicatorInstanceRequestRs
                        .builder()
                        .indicatorCategoryId(EnumIndicatorCategory.SYSTEM_CALCULATE_INDICATOR.getCode())
                        .appId(EnumString.APP_ID.getStr())
                        .indicatorName(EnumIndicatorType.SPORT_ENERGY.getDesc())
                        .displayByPercent(EnumStatus.DISABLE.getCode())
                        .def("0")
                        .core(EnumStatus.ENABLE.getCode())
                        .food(EnumStatus.DISABLE.getCode())
                        .type(EnumIndicatorType.SPORT_ENERGY.getType())
                        .min("0")
                        .max("10000")
                        .build());
                default ->
                    log.error("必须初始化指标类型枚举不存在，type:{}, desc:{}", enumIndicatorType.getType(), enumIndicatorType.getDesc());
            }
        }
        if (!createOrUpdateIndicatorInstanceRequestRsList.isEmpty()) {
            createOrUpdateIndicatorInstanceRequestRsList.sort(Comparator.comparingInt(CreateOrUpdateIndicatorInstanceRequestRs::getType));
            createOrUpdateIndicatorInstanceRequestRsList.forEach(createOrUpdateIndicatorInstanceRequestRs -> {
                try {
                    indicatorInstanceBiz.createOrUpdateRs(createOrUpdateIndicatorInstanceRequestRs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }
        /* runsix:init wuzhililn IndicatorFuncEntity */
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

