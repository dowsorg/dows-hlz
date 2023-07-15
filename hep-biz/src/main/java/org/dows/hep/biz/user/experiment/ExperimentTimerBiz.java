package org.dows.hep.biz.user.experiment;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.exception.ExperimentException;
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
     * 获取当前实验期数信息[每期开始，结束，间隔等]
     *
     * @param experimentInstanceId
     * @return
     */
    public CountDownResponse userCountdown(String experimentInstanceId) {
        // 获取当前期数
        CountDownResponse countDownResponse = new CountDownResponse();

        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentTimerEntity::getState, EnumExperimentState.ONGOING.getState())
                //.eq(ExperimentTimerEntity::getModel, 2) // 沙盘模式
                //.ne(ExperimentTimerEntity::getState, ExperimentStateEnum.FINISH.getState())
                .list();
        list = list.stream().sorted(Comparator.comparingInt(ExperimentTimerEntity::getPeriod))
                .collect(Collectors.toList());

        long ct = System.currentTimeMillis();


        for (int i = 0; i < list.size(); i++) {
            ExperimentTimerEntity pre = list.get(i);
            ExperimentTimerEntity next;
            // 最后一期
            if (i == list.size() - 1) {
                countDownResponse.setCountdown(pre.getStartTime() - ct);
                countDownResponse.setSandDuration(Double.valueOf(pre.getEndTime() - ct));
                countDownResponse.setModel(pre.getModel());
                countDownResponse.setPeriod(pre.getPeriod());
                countDownResponse.setState(pre.getState());
                break;
            }
            next = list.get(i + 1);
            // 两期之间
            if (ct >= pre.getEndTime() && ct < next.getStartTime()) {
                ct = next.getStartTime() - ct;
                countDownResponse.setCountdown(ct);
                // todo 兜底计算，在两期之间计算上一期数据,异步


                break;
            } else if (ct <= pre.getStartTime()) { // 小组分配结束
                countDownResponse.setCountdown(pre.getStartTime() - ct);
                countDownResponse.setModel(pre.getModel());
                countDownResponse.setPeriod(pre.getPeriod());
                countDownResponse.setState(pre.getState());
                break;
            } else if (ct >= pre.getStartTime() && ct <= pre.getEndTime()) { //  开始之后
                countDownResponse.setSandDuration(Double.valueOf(pre.getEndTime() - ct));
                countDownResponse.setModel(pre.getModel());
                countDownResponse.setPeriod(pre.getPeriod());
                countDownResponse.setState(pre.getState());
                break;

            }
        }
        return countDownResponse;
    }

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
    public CountDownResponse tenantCountdown(String experimentInstanceId) {
        Long sct = System.currentTimeMillis();
        CountDownResponse countDownResponse = new CountDownResponse();
        List<ExperimentSettingEntity> list = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        ExperimentSettingEntity experimentSettingEntity1 = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class.getName()))
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
            countDownResponse.setModel(EnumExperimentMode.SCHEME.getCode());
        }

        ExperimentSettingEntity experimentSettingEntity2 = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class.getName()))
                .findFirst()
                .orElse(null);

        if (experimentSettingEntity2 != null) {
            ExperimentSetting.SandSetting sandSetting =
                    JSONUtil.toBean(experimentSettingEntity2.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
            // 沙盘期数展示
            // 每期持续时长
            Map<String, Integer> durationMap = sandSetting.getDurationMap();
            // 期数
            Map<String, Integer> periodMap = sandSetting.getPeriodMap();
            // 对set排序一下
            List<String> keys = periodMap.keySet().stream().sorted().collect(Collectors.toList());
            // 每期对应的mock比列
            Map<String, Double> mockRateMap = new HashMap<>();
            int totalDay = 0;
            for (String s : keys) {
                Integer duration = durationMap.get(s);
                Integer period = periodMap.get(s);
                totalDay += period;
                double mockRate = Double.valueOf(period) / Double.valueOf(duration * 60);
                mockRateMap.put(s, mockRate);
            }

            countDownResponse.setDurationMap(durationMap);
            countDownResponse.setMockRateMap(mockRateMap);
            countDownResponse.setPeriodMap(periodMap);
            countDownResponse.setSandTime(Long.valueOf(totalDay));
            countDownResponse.setSandTimeUnit("天");
            countDownResponse.setModel(EnumExperimentMode.SAND.getCode());

            List<ExperimentTimerEntity> experimentTimerEntityList = experimentTimerService.lambdaQuery()
                    .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                    .orderByAsc(ExperimentTimerEntity::getPeriod, ExperimentTimerEntity::getPauseCount)
                    .list();

            // 如果有暂停则优先处理暂停
            ExperimentTimerEntity experimentTimerEntity = experimentTimerEntityList.stream()
                    .filter(e -> e.getPaused() == true)
                    .findFirst()
                    .orElse(null);
            // 当前时间戳-当前期数开始时间 = 相对时间（持续了多久）；将转换为秒  .. day/duration = rate
            Long second = 0L;
            if (null != experimentTimerEntity) {
                second = experimentTimerEntity.getStartTime() / 1000;
                countDownResponse.setSandDurationSecond(second);
                countDownResponse.setState(experimentTimerEntity.getState());
                countDownResponse.setPeriod(experimentTimerEntity.getPeriod());
                return countDownResponse;
            }

            Map<Integer, ExperimentTimerEntity> collect = new HashMap<>();
            experimentTimerEntityList.stream()
                    .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod))
                    .forEach((k, v) -> {
                        collect.put(k, v.stream().max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount)).get());
                    });

            collect.forEach((k, v) -> {
                // 获取比例
                //Double aFloat = mockRateMap.get(period + "");
                // 算出天数
                //Double day = second * aFloat;
                if (v.getState() == EnumExperimentState.FINISH.getState()) {
                    //countDownResponse.setSandDuration(Double.valueOf(totalDay));
                    countDownResponse.setState(v.getState());
                    countDownResponse.setPeriod(v.getPeriod());
                } else if (v.getState() == EnumExperimentState.ONGOING.getState()) {
                    // 当前时间戳-当前期数开始时间 = 相对时间（持续了多久）；将转换为秒  .. day/duration = rate
                    //Long second1 = sct - second;

                    /*Double day1 = second1 * aFloat;

                    for (int i = 0; i < period; i++) {
                        Integer integer = periodMap.get(i + "");
                        day += integer;
                    }*/
                    if (sct >= v.getStartTime() && sct <= v.getEndTime()) {
                        countDownResponse.setSandDurationSecond(sct - v.getStartTime() / 1000);
                        countDownResponse.setState(v.getState());
                        countDownResponse.setPeriod(v.getPeriod());
                    }
                }
            });
        }
        // 如果都不为空，则为标准模式
        if (experimentSettingEntity1 != null && experimentSettingEntity2 != null) {
            countDownResponse.setModel(EnumExperimentMode.STANDARD.getCode());
        }
        return countDownResponse;
    }


    /**
     * 获取当前实验期数定时器
     * ExperimentRestartRequest experimentRestartRequest
     *
     * @param experimentInstanceId
     * @return
     */
    public List<ExperimentTimerEntity> getCurrentPeriods(String experimentInstanceId) {
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                //.eq(ExperimentTimerEntity::getAppId, experimentRestartRequest.getAppId())
                .list();
        return list;
    }


    /**
     * 根据实验状态获取最后期数的计时器
     *
     * @param experimentInstanceId
     * @return
     */
    public ExperimentTimerEntity getLastPeriods(String experimentInstanceId, EnumExperimentState enumExperimentState) {
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .ne(ExperimentTimerEntity::getState, enumExperimentState.getState())
                //.eq(ExperimentTimerEntity::getAppId, experimentRestartRequest.getAppId())
                .list();
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .max(Comparator.comparingInt(ExperimentTimerEntity::getPeriod))
                .get();

        return experimentTimerEntity;
    }


    /**
     * 更新定时器及实验状态（experimentInstance,experimentParticipator）
     *
     * @param updateExperimentTimerEntities
     * @return
     */
    @DSTransactional
    public boolean saveOrUpdateExperimentTimeExperimentState(String experimentInstanceId,
                                                             List<ExperimentTimerEntity> updateExperimentTimerEntities,
                                                             EnumExperimentState enumExperimentState) {
        boolean b = experimentTimerService.saveOrUpdateBatch(updateExperimentTimerEntities);
        if (!b) {
            throw new ExperimentException(" 更新计时器实验状态发生异常！");
        }
        boolean update = experimentInstanceService.lambdaUpdate()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentInstanceEntity::getState, enumExperimentState.getState())
                .update();
        if (!update) {
            throw new ExperimentException(" 更新实验实例状态发生异常！");
        }
        boolean update1 = experimentParticipatorService.lambdaUpdate()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentParticipatorEntity::getState, enumExperimentState.getState())
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
    public ExperimentPeriodsResonse getExperimentCurrentPeriods(String appId, String experimentInstanceId) {

        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                //.eq(ExperimentTimerEntity::getAppId, appId)
                //.eq(ExperimentTimerEntity::getModel, 2) // 沙盘模式
                //.ne(ExperimentTimerEntity::getState, EnumExperimentState.FINISH.getState())
                .list();

        //List<ExperimentTimerEntity> collect = list.stream().filter(t -> t.getPauseCount() == 0).collect(Collectors.toList());
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .filter(e -> e.getStartTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= e.getEndTime())
                .max(Comparator.comparingLong(ExperimentTimerEntity::getStartTime))
                .orElse(null);

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

    /**
     * 获取实验每一期的开始时间和结束时间
     *
     * @param experimentInstanceId
     */
    public Map<Integer, ExperimentTimerEntity> getExperimentPeriodsStartAnsEndTime(String experimentInstanceId) {
        Map<Integer, ExperimentTimerEntity> map = new HashMap<>();
        List<ExperimentTimerEntity> list = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .orderByAsc(ExperimentTimerEntity::getPeriod)
                .list();
        Map<Integer, List<ExperimentTimerEntity>> collect = list.stream()
                .collect(Collectors.groupingBy(ExperimentTimerEntity::getPeriod));
        // 找出每期暂停次数最大的为准
        collect.forEach((k, v) -> {
            ExperimentTimerEntity experimentTimerEntity = v.stream()
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .get();
            map.put(k, experimentTimerEntity);
        });
        return map;
    }
}