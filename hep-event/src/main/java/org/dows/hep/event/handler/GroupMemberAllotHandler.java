package org.dows.hep.event.handler;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

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

    private int groupSize = 0;

    private static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    @Override
    public void exec(List<ExperimentParticipatorRequest> experimentParticipatorRequests) {
        // 先计数
        ExperimentContext experimentContext = ExperimentContext.getExperimentContext(experimentParticipatorRequests.get(0).getExperimentInstanceId());
        groupSize = experimentContext.getGroupCount();
        if(concurrentHashMap.containsKey(experimentParticipatorRequests.get(0).getExperimentInstanceId())){
            Integer count = (Integer) concurrentHashMap.get(experimentParticipatorRequests.get(0).getExperimentInstanceId());
            count++;
            concurrentHashMap.put(experimentParticipatorRequests.get(0).getExperimentInstanceId(),count);
        }else{
            concurrentHashMap.put(experimentParticipatorRequests.get(0).getExperimentInstanceId(),1);
        }
        if ((Integer) concurrentHashMap.get(experimentParticipatorRequests.get(0).getExperimentInstanceId()) == groupSize) {
            // 更新实验所有小组状态
            LambdaUpdateWrapper<ExperimentGroupEntity> groupWrapper = new LambdaUpdateWrapper<ExperimentGroupEntity>()
                    .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentParticipatorRequests.get(0).getExperimentInstanceId())
                    .set(ExperimentGroupEntity::getGroupState, EnumExperimentGroupStatus.COUNT_DOWN.getCode());
            experimentGroupService.update(groupWrapper);
            // 清除实验数据
            concurrentHashMap.remove(experimentParticipatorRequests.get(0).getExperimentInstanceId());
            // 通知实验所有小组
            ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
            Set<Channel> channels = userInfos.keySet();
            for (Channel channel : channels) {
                HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, experimentParticipatorRequests.get(0).getExperimentInstanceId());
            }
            log.info("开始倒计时进入实验....");
        }
    }
}
