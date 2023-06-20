package org.dows.hep.biz.user.experiment;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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

        ExperimentTimerEntity experimentTimerEntity = experimentTimerService.lambdaQuery()
                .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElse(null);

        if (experimentTimerEntity == null) {

        }
        return BeanConvert.beanConvert(experimentTimerEntity, CountDownResponse.class);
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
     * 更新定时器
     *
     * @param updateExperimentTimerEntities
     * @return
     */
    public boolean saveOrUpdateBatch(List<ExperimentTimerEntity> updateExperimentTimerEntities) {
        boolean b = experimentTimerService.saveOrUpdateBatch(updateExperimentTimerEntities);
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
                .eq(ExperimentTimerEntity::getAppId, appId)
                //.eq(ExperimentTimerEntity::getModel, 2) // 沙盘模式
                .ne(ExperimentTimerEntity::getState, ExperimentStateEnum.FINISH.getState())
                .list();

        //List<ExperimentTimerEntity> collect = list.stream().filter(t -> t.getPauseCount() == 0).collect(Collectors.toList());
        ExperimentTimerEntity experimentTimerEntity = list.stream()
                .filter(e -> e.getStartTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= e.getEndTime())
                .max(Comparator.comparingLong(ExperimentTimerEntity::getStartTime)).orElse(null);
//        List<ExperimentTimerEntity> collect1 = experimentTimerEntity1
//                .collect(Collectors.toList()).ma;
        if(null == experimentTimerEntity){
            log.error("获取实验期数异常,当前时间不存在对应的实验期数");
            StringBuilder stringBuilder = new StringBuilder();
            list.stream().forEach(k->{
                stringBuilder.append(k.getPeriod()).append("期开始时间：")
                        .append(DateUtil.date(k.getStartTime()))
                        .append(" ");
            });
            throw new ExperimentException("获取实验期数异常,当前时间不存在对应的实验期数,当前实验期数信息：" + stringBuilder);
        }
       /* if (collect1.size() > 1) {
            log.error("获取实验期数异常,当前时间存在多个期数,请检查期数配置");
            throw new ExperimentException("获取实验期数异常,当前时间存在多个期数,请检查期数配置");
        } else if (collect1.size() == 0) {
            log.error("获取实验期数异常,当前时间不存在对应的实验期数");
            StringBuilder stringBuilder = new StringBuilder();
            list.stream().forEach(k->{
                stringBuilder.append(k.getPeriod()).append("期开始时间：")
                        .append(DateUtil.date(k.getStartTime()))
                        .append(" ");
            });
            throw new ExperimentException("获取实验期数异常,当前时间不存在对应的实验期数,当前实验期数信息：" + stringBuilder);
        } else*/

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