package org.dows.hep.biz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.Notifiable;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;

/**
 * 实验通知任务
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentNoticeTask implements Runnable {

    private final Notifiable notifiable;
    private final NoticeContent noticeContent;
    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final String experimentInstanceId;

    private final Integer period;

    private final Integer noticeType;

    @Override
    public void run() {
        notifiable.notice(noticeContent);

        //更改通知任务状态
        if(noticeType == 0) {
            ExperimentTaskScheduleEntity startNoticeTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                    .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())
                    .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
                    .eq(ExperimentTaskScheduleEntity::getPeriods, period)
                    .one();
            if (startNoticeTaskScheduleEntity == null || ReflectUtil.isObjectNull(startNoticeTaskScheduleEntity)) {
                throw new ExperimentException("该通知任务不存在");
            }
            experimentTaskScheduleService.lambdaUpdate()
                    .eq(ExperimentTaskScheduleEntity::getId, startNoticeTaskScheduleEntity.getId())
                    .set(ExperimentTaskScheduleEntity::getExecuted,true)
                    .update();
        }
        if(noticeType == 1) {
            ExperimentTaskScheduleEntity endNoticeTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                    .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())
                    .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
                    .eq(ExperimentTaskScheduleEntity::getPeriods, period)
                    .one();
            if (endNoticeTaskScheduleEntity == null || ReflectUtil.isObjectNull(endNoticeTaskScheduleEntity)) {
                throw new ExperimentException("该通知任务不存在");
            }
            experimentTaskScheduleService.lambdaUpdate()
                    .eq(ExperimentTaskScheduleEntity::getId, endNoticeTaskScheduleEntity.getId())
                    .set(ExperimentTaskScheduleEntity::getExecuted,true)
                    .update();
        }
    }

}
