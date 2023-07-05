package org.dows.hep.biz.util;

import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.sequence.api.IdGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PeriodsTimerUtil {

    /**
     * 构建期数对应的计时器
     *
     * @param experimentInstance
     * @param experimentSetting
     * @param experimentTimerEntities
     */
    public static void buildPeriods(ExperimentInstanceEntity experimentInstance, ExperimentSetting experimentSetting,
                                    List<ExperimentTimerEntity> experimentTimerEntities, IdGenerator idGenerator) {
        ExperimentSetting.SandSetting sandSetting = experimentSetting.getSandSetting();
        if (null == sandSetting) {
            throw new BizException("沙盘模式，sandSetting为不能为空!");
        }
        // 获取总期数，生成每期的计时器
        Integer periodCount = sandSetting.getPeriods();
        // 每期间隔/秒*1000
        Long interval = sandSetting.getInterval() * 1000;
        // 每期时长/分钟
        Map<String, Integer> durationMap = sandSetting.getDurationMap();
        // 实验开始时间
        long startTime = experimentInstance.getStartTime().getTime();

        // 一期开始时间=实验开始时间-方案设计时间,第一期没有间隔时间
        long pst = 0L;
        // 定义一期结束时间
        long pet = 0L;
        // 如果是标准模式，那么沙盘期数 需要减去方案设计截止时间
        if (experimentInstance.getModel() == EnumExperimentMode.STANDARD.getCode()) {
            ExperimentSetting.SchemeSetting schemeSetting = experimentSetting.getSchemeSetting();
            if (schemeSetting != null) {
                // 方案设计截止时间
                long time1 = schemeSetting.getSchemeEndTime().getTime();
                // 如果是标准模式，一期开始时间 = 方案设计截止时间 - 实验开始时间,第一期没有间隔时间 + 间隔时间
                //pst = time1 - startTime +  interval;
                // 如果是标准模式，一期开始时间 = 方案设计截止时间 + 间隔时间
                pst = time1 + interval;
                // 如果是标准模式，一期开始时间 = 实验开始时间 + 方案设计时长结束时间 + 间隔时间
                //long duration = schemeSetting.getDuration() * 60 * 1000;
                //pst = startTime + duration + interval;
            }
        } else {
            pst = startTime + interval;
        }

        List<Integer> periods = durationMap.keySet().stream()
                .map(p -> Integer.valueOf(p)).sorted().collect(Collectors.toList());
        if (periodCount != periods.size()) {
            throw new ExperimentException("分配实验异常,期数与期数时间设置不匹配");
        }
        for (Integer period : periods) {
            // 一期结束时间 = 一期开始时间 + 一期持续时间
            pet = pst + durationMap.get(period + "") * 60 * 1000;
            ExperimentTimerEntity experimentTimerEntity = ExperimentTimerEntity.builder()
                    .appId(experimentInstance.getAppId())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .experimentTimerId(idGenerator.nextIdStr())
                    .periodInterval(interval)
                    .period(period)
                    .model(EnumExperimentMode.STANDARD.getCode())
                    .state(EnumExperimentState.UNBEGIN.getState())
                    .startTime(pst)
                    .endTime(pet)
                    .build();
            experimentTimerEntities.add(experimentTimerEntity);
            // 下一期开始时间 = 上一期结束时间+间隔时间
            pst = pet + interval;
        }
    }
}
