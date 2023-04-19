package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTimerService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author lait.zhang
 * @description project descr:实验:实验计时器
 * @date 2023年4月18日 上午10:45:07
 */
@RequiredArgsConstructor
@Service
public class ExperimentTimerBiz {

    private final ExperimentInstanceService experimentInstanceService;

    private final ExperimentGroupService experimentGroupService;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentTimerService experimentTimerService;

    private final IdGenerator idGenerator;
    /**
     * @param
     * @return
     * @说明: 获取实验倒计时
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public CountDownResponse countdown(String experimentInstanceId) {
        return new CountDownResponse();
    }


    /**
     * @param
     * @return
     * @说明: 开始实验
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean start(String experimentInstanceId) {

        // todo websocket 开始实验，通知客户端
        /**
         * todo
         * 0.查询实验，设置实验状态为暂停，
         * 1.查询实验的所有小组参与者信息，
         * 2.websocket 通知所有客户端
         */
        Optional<ExperimentInstanceEntity> optionalExperimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId).oneOpt();
        if (!optionalExperimentInstanceEntity.isPresent()) {
            throw new RuntimeException("实验不存在");
        }
        ExperimentInstanceEntity experimentInstanceEntity = optionalExperimentInstanceEntity.get();
        experimentInstanceEntity.setState(1);
        boolean update = experimentInstanceService.lambdaUpdate().update(experimentInstanceEntity);
        if (!update) {
            throw new RuntimeException("实验状态更新失败");
        }

        List<ExperimentParticipatorEntity> list = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .list();

        if (list.size() < 0) {
            throw new RuntimeException("实验组员不存在");
        }
        // todo 通知所有实验人员暂停



        // 实验计时器
        ExperimentTimerEntity experimentTimerEntity = ExperimentTimerEntity.builder()
                .experimentTimerId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentInstanceId)
//                .startTime()
//                .periods()
                .build();
        experimentTimerService.save(experimentTimerEntity);

        return true;
    }


    /**
     * @param
     * @return
     * @说明: 暂停实验
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean pause(String experimentInstanceId) {

        // todo websocket 暂停实验，通知客户端

        return true;
    }
}