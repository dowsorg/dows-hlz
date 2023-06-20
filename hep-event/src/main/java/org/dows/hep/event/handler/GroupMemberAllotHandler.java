package org.dows.hep.event.handler;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 小组成员分配机构处理器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class GroupMemberAllotHandler extends AbstractEventHandler implements EventHandler<List<ExperimentParticipatorRequest>> {

    private final ExperimentGroupService experimentGroupService;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final StartHandler startHandler;

    private int groupSize = 0;

    private static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    @Override
    public void exec(List<ExperimentParticipatorRequest> participatorRequestList) {
        String experimentInstanceId = participatorRequestList.get(0).getExperimentInstanceId();
        // 先计数
        startHandler.exec(ExperimentRestartRequest.builder()
                .appId("3")
                .experimentInstanceId(experimentInstanceId)
                .paused(false)
                .currentTime(new Date())
                .build());
        ExperimentContext experimentContext = ExperimentContext.getExperimentContext(experimentInstanceId);
        groupSize = experimentContext.getGroupCount();
        if (concurrentHashMap.containsKey(experimentInstanceId)) {
            Integer count = (Integer) concurrentHashMap.get(experimentInstanceId);
            count++;
            concurrentHashMap.put(experimentInstanceId, count);
        } else {
            concurrentHashMap.put(experimentInstanceId, 1);
        }
        if ((Integer) concurrentHashMap.get(experimentInstanceId) == groupSize) {
            // 更新实验所有小组状态
            LambdaUpdateWrapper<ExperimentGroupEntity> groupWrapper = new LambdaUpdateWrapper<ExperimentGroupEntity>()
                    .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentInstanceId)
                    .set(ExperimentGroupEntity::getGroupState, EnumExperimentGroupStatus.COUNT_DOWN.getCode());
            experimentGroupService.update(groupWrapper);
            // 清除实验数据
            concurrentHashMap.remove(experimentInstanceId);
            // 查询到对应的accountId
            Set<String> accountIds = new HashSet<>();
            participatorRequestList.forEach(participator->{
                List<ExperimentParticipatorEntity> participatorList = experimentParticipatorService.lambdaQuery()
                        .eq(ExperimentParticipatorEntity::getExperimentInstanceId,experimentInstanceId)
                        .eq(ExperimentParticipatorEntity::getDeleted,false)
                        .list();
                participatorList.forEach(participator1->{
                    accountIds.add(participator1.getAccountId());
                });
            });


            // 通知实验所有小组
            ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();

            // 过滤数据，只给学生发websocket
            Set<Channel> channels = userInfos.keySet();
            for (Channel channel : channels) {
                if (accountIds.contains(userInfos.get(channel).getAccountName())) {
                    ExperimentPeriodsResonse periodsResonse = experimentTimerBiz.getExperimentPeriods("3",experimentInstanceId);
                    StartCutdownResponse startCutdownResponse = new StartCutdownResponse();
                    startCutdownResponse.setEnumWebSocketType(EnumWebSocketType.START_EXPERIMENT_COUNTDOWN);
                    startCutdownResponse.setExperimentPeriodsResonse(periodsResonse);
                    Response<StartCutdownResponse> ok = Response.ok(startCutdownResponse);
                    HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, ok);
                }
            }
            log.info("开始倒计时进入实验....");
        }
    }

    @Data
    public static class StartCutdownResponse{
        EnumWebSocketType enumWebSocketType;

        ExperimentPeriodsResonse experimentPeriodsResonse;
    }
}
