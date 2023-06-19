package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.ParticipatorTypeEnum;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * todo
 * 实验暂停事件，
 * 1.websocket 通知客户端，禁止操作，同时根据实验ID,服务端拦截器中禁用该实验ID的客户端请求
 * 2.服务端记录/更新实验暂停时间，ExperimentTimer
 * 3.停止相关的任务和事件的计时器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TeamNameHandler extends AbstractEventHandler implements EventHandler<CreateGroupRequest> {

    private final ExperimentParticipatorService experimentParticipatorService;

    @Override
    public void exec(CreateGroupRequest createGroupRequest) {
//       //1、获取该小组组员
//        List<ExperimentParticipatorEntity> entities = experimentParticipatorService.lambdaQuery()
//                .eq(ExperimentParticipatorEntity::getExperimentGroupId, createGroupRequest.getExperimentGroupId())
//                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, createGroupRequest.getExperimentInstanceId())
//                .eq(ExperimentParticipatorEntity::getParticipatorType, ParticipatorTypeEnum.STUDENT.getCode())
//                .eq(ExperimentParticipatorEntity::getDeleted, false)
//                .list();
        //2、向学生发送websocket消息

    }
}