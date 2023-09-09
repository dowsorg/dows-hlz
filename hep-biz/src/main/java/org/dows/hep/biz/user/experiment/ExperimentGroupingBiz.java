package org.dows.hep.biz.user.experiment;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumParticipatorType;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/9/9 19:03
 */
@Component
@RequiredArgsConstructor
public class ExperimentGroupingBiz {

    private final IdGenerator idGenerator;

    private final ApplicationEventPublisher applicationEventPublisher;

    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验小组
    private final ExperimentGroupService experimentGroupService;
    @DSTransactional
    public Boolean grouping(ExperimentGroupSettingRequest experimentGroupSettingRequest) {

        Long delay = experimentGroupSettingRequest.getStartTime().getTime() - System.currentTimeMillis();
        if (delay < 0) {
            throw new ExperimentException("实验时间设置错误,实验开始时间小于当前时间!为确保实验正常初始化，开始时间至少大于当前时间1分钟");
        }

        List<ExperimentGroupSettingRequest.GroupSetting> experimentGroupSettings = experimentGroupSettingRequest.getGroupSettings();
        List<ExperimentGroupEntity> experimentGroupEntitys = new ArrayList<>();
        Map<String, List<ExperimentParticipatorEntity>> groupParticipators = new HashMap<>();
        for (ExperimentGroupSettingRequest.GroupSetting groupSetting : experimentGroupSettings) {
            ExperimentGroupEntity experimentGroupEntity = ExperimentGroupEntity.builder()
                    .appId(experimentGroupSettingRequest.getAppId())
                    .experimentGroupId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                    .groupAlias(groupSetting.getGroupAlias())
                    .memberCount(groupSetting.getMemberCount())
                    .groupState(EnumExperimentGroupStatus.GROUP_RENAME.getCode())
                    .groupNo(groupSetting.getGroupNo())
                    //.groupName(groupSetting.getGroupName())
                    .build();
            experimentGroupEntitys.add(experimentGroupEntity);


            //todo
            List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
            List<ExperimentGroupSettingRequest.ExperimentParticipator> experimentParticipators = groupSetting.getExperimentParticipators();
            for (ExperimentGroupSettingRequest.ExperimentParticipator experimentParticipator : experimentParticipators) {
                ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                        .experimentParticipatorId(idGenerator.nextIdStr())
                        .appId(experimentGroupSettingRequest.getAppId())
                        .model(experimentGroupSettingRequest.getModel())
                        .caseInstanceId(experimentGroupSettingRequest.getCaseInstanceId())
                        .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                        .experimentName(experimentGroupSettingRequest.getExperimentName())
                        .experimentStartTime(experimentGroupSettingRequest.getStartTime())
                        .accountId(experimentParticipator.getParticipatorId())
                        .accountName(experimentParticipator.getParticipatorName())
                        .groupNo(groupSetting.getGroupNo())
                        .groupAlias(groupSetting.getGroupAlias())
                        .state(EnumExperimentState.UNBEGIN.getState())
                        .experimentGroupId(experimentGroupEntity.getExperimentGroupId())
                        .participatorType(EnumParticipatorType.STUDENT.getCode())
                        .build();
                // 如果是0【第一个人】设置为组长
                if (experimentParticipator.getSeq() == 0) {
                    experimentParticipatorEntity.setParticipatorType(EnumParticipatorType.CAPTAIN.getCode());
                }
                experimentParticipatorEntityList.add(experimentParticipatorEntity);
                // 记录每组对应的组员——
                groupParticipators.put(experimentGroupEntity.getExperimentGroupId(), experimentParticipatorEntityList);
            }
        }
        List<ExperimentParticipatorEntity> collect = groupParticipators.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList());

        List<String> accountIds = collect.stream().map(ExperimentParticipatorEntity::getAccountId).collect(Collectors.toList());
        /**
         * 同一时刻，一个用户职能参与到一个实验中
         */
        List<ExperimentParticipatorEntity> list = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentStartTime, experimentGroupSettingRequest.getStartTime())
                .eq(ExperimentParticipatorEntity::getCaseInstanceId, experimentGroupSettingRequest.getCaseInstanceId())
                .in(ExperimentParticipatorEntity::getAccountId, accountIds)
                .list();
        if (list.size() > 0) {
            List<String> collect1 = list.stream().map(ExperimentParticipatorEntity::getAccountName).collect(Collectors.toList());
            List<String> collect2 = list.stream().map(ExperimentParticipatorEntity::getExperimentName).collect(Collectors.toList());
            throw new ExperimentException("当前用户: " + String.join(",", collect1) + "在同一时刻已参与实验: " + String.join(",", collect2));
        }

        // 保存实验小组
        experimentGroupService.saveBatch(experimentGroupEntitys);
        // 保存实验参与人[学生]
        experimentParticipatorService.saveBatch(collect);

        // 发布实验init事件
        //applicationEventPublisher.publishEvent(new InitializeEvent(experimentGroupSettingRequest));
        return true;
    }
}
