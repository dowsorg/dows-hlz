package org.dows.hep.biz.user.experiment;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:实验计时器
 * @date 2023年4月23日 上午9:44:34
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentTimerBiz {

    private final ExperimentTimerService experimentTimerService;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentSettingService experimentSettingService;


    /**
     * @param
     * @return
     * @说明: 获取实验倒计时
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public CountDownResponse countdown(String experimentInstanceId) {
        CountDownResponse countDownResponse = new CountDownResponse();

        List<ExperimentSettingEntity> list = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        ExperimentSettingEntity experimentSettingEntity1 = list.stream().filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class))
                .findFirst()
                .orElse(null);
        if (experimentSettingEntity1 != null) {
            ExperimentSetting.SchemeSetting schemeSetting =
                    JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
            if (System.currentTimeMillis() > schemeSetting.getSchemeEndTime().getTime()) {
                countDownResponse.setSchemeTime(0L);
            } else {
                // 方案设计倒计时
                Long schemeTime = schemeSetting.getSchemeEndTime().getTime() - System.currentTimeMillis();
                countDownResponse.setSchemeTime(schemeTime);
            }
        }

        ExperimentSettingEntity experimentSettingEntity2 = list.stream().filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class))
                .findFirst()
                .orElse(null);

        if (experimentSettingEntity2 != null) {
            ExperimentSetting.SandSetting sandSetting =
                    JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
            // 沙盘期数展示
            // 每期持续时长
            Map<String, Integer> durationMap = sandSetting.getDurationMap();
            // 期数
            Map<String, Integer> periodMap = sandSetting.getPeriodMap();
            // 对set排序一下
            List<String> keys = periodMap.keySet().stream().sorted().collect(Collectors.toList());
            // 每期对应的mock比列
            Map<String, Float> mockRateMap = new HashMap<>();
            int totalDay = 0;
            for (String s : keys) {
                Integer duration = durationMap.get(s);
                Integer period = periodMap.get(s);
                totalDay += period;
                float mockRate = period / duration;
                mockRateMap.put(s, mockRate);
            }

            countDownResponse.setSandTime(Long.valueOf(totalDay));
            countDownResponse.setSandTimeUnit("天");

            // 如果沙盘未开始，直接返回0,或大于结束如果开始，则返回对应的持续时间
            // 获取当前期数
            List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerService.lambdaQuery()
                    .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                    .list();

            for (ExperimentTimerEntity experimentTimerEntity : experimentTimerEntityList) {
                if (experimentTimerEntity.getState() == ExperimentStateEnum.FINISH.getState()) {
                    countDownResponse.setSandDuration(Float.valueOf(totalDay));
                    break;
                } else if (experimentTimerEntity.getState() == ExperimentStateEnum.UNBEGIN.getState()) {
                    countDownResponse.setSandDuration(0F);
                    break;
                } else if (experimentTimerEntity.getState() == ExperimentStateEnum.ONGOING.getState()) {
                    // 当前时间戳-当前期数开始时间 = 相对时间
                    Long ct = System.currentTimeMillis() - experimentTimerEntity.getStartTime();
                    // 将ct转换为分钟数
                    Long minute = ct / 1000 / 60;
                    // 获取比例
                    Float aFloat = mockRateMap.get(experimentTimerEntity.getPeriod() + "");
                    float day = minute * aFloat;
                    Integer period = experimentTimerEntity.getPeriod();
                    for (int i = 1; i <= period; i++) {
                        Integer integer = periodMap.get(i + "");
                        day += integer;
                    }
                    countDownResponse.setSandDuration(day);
                    break;
                }
            }
        }

        return countDownResponse;
      /*  ExperimentTimerEntity experimentTimerEntity = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElse(null);

        if (experimentTimerEntity == null) {

        }
        return BeanConvert.beanConvert(experimentTimerEntity, CountDownResponse.class);*/
    }


    /**
     * 获取当前实验期数定时器
     *
     * @param experimentRestartRequest
     * @return
     */
    public List<ExperimentTimerEntity> getCurrentPeriods(ExperimentRestartRequest experimentRestartRequest) {
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentRestartRequest.getExperimentInstanceId())
                .eq(ExperimentTimerEntity::getAppId, experimentRestartRequest.getAppId())
                .list();
        return list;
    }


    /**
     * 更新定时器及实验状态（experimentInstance,experimentParticipator）
     *
     * @param updateExperimentTimerEntities
     * @return
     */
    @DSTransactional
    public boolean saveOrUpdateExperimentTimeExperimentState(String experimentInstanceId, List<ExperimentTimerEntity> updateExperimentTimerEntities, ExperimentStateEnum experimentStateEnum) {
        boolean b = experimentTimerService.saveOrUpdateBatch(updateExperimentTimerEntities);
        if (!b) {
            throw new ExperimentException(" 更新计时器实验状态发生异常！");
        }
        boolean update = experimentInstanceService.lambdaUpdate().eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentInstanceEntity::getState, experimentStateEnum.getState())
                .update();
        if (!update) {
            throw new ExperimentException(" 更新实验实例状态发生异常！");
        }
        boolean update1 = experimentParticipatorService.lambdaUpdate().eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentParticipatorEntity::getState, experimentStateEnum.getState())
                .update();

        if (!update1) {
            throw new ExperimentException(" 更新实验参与者实验状态发生异常！");
        }
        return b;
    }


    /**
     * 获取当前实验期数信息[每期开始，结束，间隔等]
     *
     * @param appId
     * @param experimentInstanceId
     * @return
     */
    public ExperimentPeriodsResonse getExperimentPeriods(String appId, String experimentInstanceId) {

        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                //.eq(ExperimentTimerEntity::getAppId, appId)
                //.eq(ExperimentTimerEntity::getModel, 2) // 沙盘模式
                .ne(ExperimentTimerEntity::getState, ExperimentStateEnum.FINISH.getState())
                .list();

        //List<ExperimentTimerEntity> collect = list.stream().filter(t -> t.getPauseCount() == 0).collect(Collectors.toList());
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .filter(e -> e.getStartTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= e.getEndTime())
                .max(Comparator.comparingLong(ExperimentTimerEntity::getStartTime)).orElse(null);
//        List<ExperimentTimerEntity> collect1 = experimentTimerEntity1
//                .collect(Collectors.toList()).ma;
        if (null == experimentTimerEntity) {
            log.error("获取实验期数异常,当前时间不存在对应的实验期数");
            StringBuilder stringBuilder = new StringBuilder();
            list.stream().forEach(k -> {
                stringBuilder.append(k.getPeriod()).append("期开始时间：")
                        .append(DateUtil.date(k.getStartTime()))
                        .append(" ");
            });
            throw new ExperimentException("获取实验期数异常,当前时间不存在对应的实验期数,当前实验期数信息：" + stringBuilder);
        }
        // 获取当前期数
        //ExperimentTimerEntity experimentTimerEntity = collect1.get(0);
        ExperimentPeriodsResonse experimentPeriodsResonse = new ExperimentPeriodsResonse();
        experimentPeriodsResonse.setCurrentPeriod(experimentTimerEntity.getPeriod());
        experimentPeriodsResonse.setExperimentInstanceId(experimentTimerEntity.getExperimentInstanceId());

        List<ExperimentPeriodsResonse.ExperimentPeriods> experimentPeriods = BeanConvert
                .beanConvert(list, ExperimentPeriodsResonse.ExperimentPeriods.class);
        experimentPeriodsResonse.setExperimentPeriods(experimentPeriods);
        return experimentPeriodsResonse;

    }
}