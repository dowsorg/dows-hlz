package org.dows.hep.biz.task;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;

/**
 * @author fhb
 * @version 1.0
 * @description 实验 `方案设计` 到期执行任务 - 指定作答时间结束
 * @date 2023/7/27 14:25
 **/
@Slf4j
@RequiredArgsConstructor
public class ExptSchemeFinishTask implements Runnable {
    private final ExperimentTaskScheduleService experimentTaskScheduleService;
    private final ExperimentSchemeBiz experimentSchemeBiz;

    private final String exptInstanceId;
    private final String exptGroupId;

    /**
     * @author fhb
     * @description 实验 `方案设计` 规定作答时间到了时，自动提交。
     * @date 2023/7/27 14:27
     */
    @Override
    public void run() {
        // 当剩余时间为0时提交
        boolean submitRes = experimentSchemeBiz.submitWhen0RemainingTime(exptInstanceId, exptGroupId);
        if(!submitRes) {
            log.error("实验-方案设计作答剩余时间为0时自动提交任务：更新方案设计状态时发生异常");
            throw new BizException("实验-方案设计作答剩余时间为0时自动提交任务：更新方案设计状态时发生异常");
        }

        //更改实验任务状态
        ExperimentTaskScheduleEntity exptSchemeFinishTaskEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.exptSchemeFinishTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, exptInstanceId)
                .isNull(ExperimentTaskScheduleEntity::getPeriods)
                .one();
        if (BeanUtil.isEmpty(exptSchemeFinishTaskEntity)) {
            log.error("实验-方案设计作答剩余时间为0时自动提交任务：获取数据库执行任务数据异常");
            throw new ExperimentException("实验-方案设计作答剩余时间为0时自动提交任务：获取数据库执行任务数据异常");
        }

        boolean updateRes = experimentTaskScheduleService.lambdaUpdate()
                .eq(ExperimentTaskScheduleEntity::getId, exptSchemeFinishTaskEntity.getId())
                .set(ExperimentTaskScheduleEntity::getExecuted, true)
                .update();
        if (!updateRes) {
            log.error("实验-方案设计作答剩余时间为0时自动提交任务：更新数据库状态异常");
            throw new BizException("实验-方案设计作答剩余时间为0时自动提交任务：更新数据库状态异常");
        }
    }
}
