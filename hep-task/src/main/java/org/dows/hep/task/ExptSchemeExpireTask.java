package org.dows.hep.task;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;

/**
 * @author fhb
 * @version 1.0
 * @description 实验 `方案设计` 到期执行任务 - 截止作答时间结束
 * @date 2023/7/26 9:41
 **/

@Slf4j
@RequiredArgsConstructor
public class ExptSchemeExpireTask implements Runnable {
    private final ExperimentTaskScheduleService experimentTaskScheduleService;
    private final ExperimentSchemeBiz experimentSchemeBiz;

    private final String exptInstanceId;

    /**
     * @description 实验 `方案设计` 到截止时间自动提交
     * @date 2023/7/26 10:38
     */
    @Override
    public void run() {
        // 批量更新实验下所有方案设计的提交状态
        Boolean submitRes = experimentSchemeBiz.submitBatchWhenExpire(exptInstanceId);
        if(!submitRes) {
            log.error("实验-方案设计到截止时间自动提交任务：批量更新方案设计状态时发生异常");
//            throw new BizException("实验-方案设计到截止时间自动提交任务：批量更新方案设计状态时发生异常");
        }

        //更改实验任务状态
        ExperimentTaskScheduleEntity exptSchemeExpireTaskEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.exptSchemeExpireTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, exptInstanceId)
                .isNull(ExperimentTaskScheduleEntity::getPeriods)
                .one();
        if (BeanUtil.isEmpty(exptSchemeExpireTaskEntity)) {
            log.error("实验-方案设计到截止时间自动提交任务：获取数据库执行任务数据异常");
//            throw new ExperimentException("实验-方案设计到截止时间自动提交任务：获取数据库执行任务数据异常");
        }

        boolean updateRes = experimentTaskScheduleService.lambdaUpdate()
                .eq(ExperimentTaskScheduleEntity::getId, exptSchemeExpireTaskEntity.getId())
                .set(ExperimentTaskScheduleEntity::getExecuted, true)
                .update();
        if (!updateRes) {
            log.error("实验-方案设计到截止时间自动提交任务：更新数据库状态异常");
//            throw new BizException("实验-方案设计到截止时间自动提交任务：更新数据库状态异常");
        }
    }
}
