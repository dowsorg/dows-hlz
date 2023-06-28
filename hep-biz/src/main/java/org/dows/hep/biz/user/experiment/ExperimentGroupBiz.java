package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.event.ExptQuestionnaireAllotEvent;
import org.dows.hep.api.event.GroupMemberAllotEvent;
import org.dows.hep.api.event.source.ExptQuestionnaireAllotEventSource;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.exception.ExperimentParticipatorException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.request.AllotActorRequest;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:实验小组
 * @date 2023年4月18日 上午10:45:07
 */
@RequiredArgsConstructor
@Service
public class ExperimentGroupBiz {

    private final ExperimentGroupService experimentGroupService;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentSettingService experimentSettingService;

    private final ExperimentActorService experimentActorService;

    private final ExperimentInstanceService experimentInstanceService;

    private final IdGenerator idGenerator;

    private final ExperimentOrgService experimentOrgService;

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * @param
     * @return
     * @说明: 创建团队
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @DSTransactional
    public Boolean createGroup(CreateGroupRequest createGroup) {

        //todo 判断这个实验是存在的，这个小组是存在的，并绑定在这个实验上的否则不能创建
        List<ExperimentGroupEntity> list = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, createGroup.getExperimentInstanceId())
                .eq(ExperimentGroupEntity::getExperimentGroupId, createGroup.getExperimentGroupId())
                .list();

        // todo 并且这个的账号是队长
        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, createGroup.getExperimentGroupId())
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, createGroup.getExperimentInstanceId())
                .eq(ExperimentParticipatorEntity::getAccountId, createGroup.getAccountId())
                .eq(ExperimentParticipatorEntity::getDeleted, false)
                .eq(ExperimentParticipatorEntity::getParticipatorType, ParticipatorTypeEnum.CAPTAIN.getCode())
                .oneOpt().orElse(null);

        if (list.size() == 0) {
            throw new ExperimentException(ExperimentStatusCode.NO_EXIST_GROUP_ID);
        }
        if (experimentParticipatorEntity == null) {
            throw new ExperimentException(ExperimentStatusCode.NOT_CAPTAIN);
        }
        // 发送websocket消息给组员
//        applicationEventPublisher.publishEvent(new TeamNameEvent(createGroup));
        // 更新组名和状态
        return experimentGroupService.lambdaUpdate()
                .eq(ExperimentGroupEntity::getExperimentGroupId, createGroup.getExperimentGroupId())
                .eq(ExperimentGroupEntity::getExperimentInstanceId, createGroup.getExperimentInstanceId())
                .update(ExperimentGroupEntity.builder()
                        .groupState(EnumExperimentGroupStatus.ASSIGN_DEPARTMENT.getCode())
                        .groupName(createGroup.getGroupName())
                        .build());
    }

    /**
     * @param
     * @return
     * @说明: 扮演角色
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean actorRole(AllotActorRequest allotActorRequest) {
        List<ExperimentActorEntity> experimentActorEntityList = new ArrayList<>();
        allotActorRequest.getActorOrgMap().forEach((accountInfo, orgs) -> {
            for (AllotActorRequest.CaseOrgInfo org : orgs) {
                ExperimentActorEntity experimentActorEntity = ExperimentActorEntity.builder()
                        .experimentActorId(idGenerator.nextIdStr())
                        .experimentInstanceId(allotActorRequest.getExperimentInstanceId())
                        .experimentGroupId(allotActorRequest.getExperimentGroupId())
                        // 账号ID
//                        .accountId(accountInfo.getId())
//                        .accountName(accountInfo.getAccountName())

                        .build();
                // 扮演类型[0:问题，1:机构]
                if (1 == 1) {
//                    experimentActorEntity.setActorId(org.getPrincipalId());
//                    experimentActorEntity.setActorType();
                }
                // 如果是[0:教师，1:组长，2：学生]
                if (1 == 1) {
                    experimentActorEntity.setParticipatorType(2);
                }
                experimentActorEntityList.add(experimentActorEntity);
            }
        });
        // todo 批量保存
        boolean b = experimentActorService.saveOrUpdateBatch(experimentActorEntityList);
        return b;
    }

    /**
     * @param
     * @return
     * @说明: 获取实验小组列表
     * @关联表:experiment_group
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<ExperimentGroupResponse> listGroup(String experimentInstanceId) {
        List<ExperimentGroupEntity> entities = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentGroupEntity::getDeleted, false)
                .list();
        //复制属性
        List<ExperimentGroupResponse> experimentGroupResponse = new ArrayList<>();
        if (entities != null && entities.size() > 0) {
            entities.forEach(entity -> {
                ExperimentGroupResponse response = new ExperimentGroupResponse();
                BeanUtil.copyProperties(entity, response);
                experimentGroupResponse.add(response);
            });
        }
        return experimentGroupResponse;
    }

    /**
     * @param
     * @return
     * @说明: 获取实验小组列表
     * @关联表:
     * @工时: 0H
     * @开发者:
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<ExperimentParticipatorResponse> pageParticipators(Page<ExperimentParticipatorEntity> page, String experimentInstanceId) {
        LambdaQueryChainWrapper<ExperimentParticipatorEntity> eq = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .groupBy(ExperimentParticipatorEntity::getExperimentGroupId)
                .orderByAsc(ExperimentParticipatorEntity::getGroupNo);
        Page page1 = experimentParticipatorService.page(page, eq);
        return page1;
    }

    /**
     * @param
     * @return
     * @说明: 获取小组组员列表
     * @关联表: experiment_participator
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月6日 上午16:28:07
     */
    public List<ExperimentParticipatorResponse> listGroupMembers(String experimentGroupId, String experimentInstanceId) {
        List<ExperimentParticipatorEntity> entities = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getDeleted, false)
                .list();
        //复制属性
        List<ExperimentParticipatorResponse> experimentParticipatorResponses = new ArrayList<>();
        if (entities != null && entities.size() > 0) {
            entities.forEach(entity -> {
                ExperimentParticipatorResponse response = new ExperimentParticipatorResponse();
                BeanUtil.copyProperties(entity, response);
                experimentParticipatorResponses.add(response);
            });
        }
        return experimentParticipatorResponses;
    }

    /**
     * @param
     * @return
     * @说明: 获取某个实验中某个小组的机构列表
     * @关联表: experiment_org
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月9日 下午13:34:07
     */
    public List<ExperimentOrgEntity> listExperimentGroupOrg(String experimentGroupId, String experimentInstanceId) {
        List<ExperimentOrgEntity> entities = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
//                .eq(ExperimentOrgEntity::getPeriods, periods)
                .eq(ExperimentOrgEntity::getDeleted, false)
                .list();
        return entities;
    }

    /**
     * @param
     * @return
     * @说明: 分配小组成员
     * @关联表: experiment_participator
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上10:13:07
     */
    @DSTransactional
    public Boolean allotGroupMembers(List<ExperimentParticipatorRequest> participatorList) {
        /**
         * todo 优化一下参数结构@jx
         */
        String experimentInstanceId = participatorList.get(0).getExperimentInstanceId();
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId,experimentInstanceId)
                .oneOpt().orElseThrow(()->new ExperimentException("实验不存在"));
        if(experimentInstanceEntity.getState() == ExperimentStateEnum.UNBEGIN.getState()){
            throw new ExperimentException("实验未开始，不能分配小组成员");
        }
        List<ExperimentParticipatorEntity> entityList = new ArrayList<>();
        // todo 后面改调，批量查，批量更新
        participatorList.forEach(request -> {
            ExperimentParticipatorEntity model = experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getExperimentParticipatorId, request.getExperimentParticipatorId())
                    //.eq(ExperimentParticipatorEntity::getAppId, null == request.getAppId() ? "3" : request.getAppId())
                    .eq(ExperimentParticipatorEntity::getExperimentGroupId, request.getExperimentGroupId())
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                    .eq(ExperimentParticipatorEntity::getDeleted, false)
                    .oneOpt()
                    .orElse(null);
            if (model == null || ReflectUtil.isObjectNull(model)) {
                throw new ExperimentParticipatorException(EnumExperimentParticipator.PARTICIPATOR_NOT_EXIST_EXCEPTION);
            }
            ExperimentParticipatorEntity entity = ExperimentParticipatorEntity.builder()
                    .experimentOrgIds(request.getExperimentOrgIds())
                    .experimentOrgNames(request.getExperimentOrgNames())
                    .participatorState(3)
                    .id(model.getId())
                    .build();
            entityList.add(entity);
            /**
             *  todo 更新组状态为等待其他小组分配完成
             *  小组状态 [0-新建（待重新命名） 1-编队中 （分配成员角色） 2-编队完成 3-已锁定 4-已解散]
             */
            experimentGroupService.lambdaUpdate()
                    .eq(ExperimentGroupEntity::getExperimentGroupId, request.getExperimentGroupId())
                    .eq(ExperimentGroupEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                    .update(ExperimentGroupEntity.builder()
                            .groupState(EnumExperimentGroupStatus.WAIT_ALL_GROUP_ASSIGN.getCode())
                            .build());
        });
        boolean b = experimentParticipatorService.updateBatchById(entityList);
        if (!b) {
            return false;
        }
        List<ExperimentGroupEntity> list = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, participatorList.get(0).getExperimentInstanceId())
                .list();
        List<ExperimentGroupEntity> collect = list.stream()
                .filter(e -> e.getGroupState() == EnumExperimentGroupStatus.WAIT_ALL_GROUP_ASSIGN.getCode())
                .collect(Collectors.toList());
        // 所有小组准备完成发布事件，计数小组是否分配到齐，是否都分配好
        if (list.size() == collect.size()) {

            /**
             * todo 该处应该为发布一个事件，名称为 开始实验事件，在该事件中处理通知客户端和（分配试卷？？？？应该提前完成？）
             */
            applicationEventPublisher.publishEvent(new GroupMemberAllotEvent(participatorList));
            /**
             * todo 分配试卷事件，可以合并后面需要优化//
             */
            applicationEventPublisher.publishEvent(new ExptQuestionnaireAllotEvent(
                    ExptQuestionnaireAllotEventSource.builder()
                            .experimentInstanceId(participatorList.get(0).getExperimentInstanceId())
                            .experimentGroupId(participatorList.get(0).getExperimentGroupId())
                            .build()));
            return true;

            /*String experimentInstanceId = participatorList.get(0).getExperimentInstanceId();
            // 更新实验、参与者状态为ongoing
            experimentInstanceService.lambdaUpdate()
                    .eq(ExperimentInstanceEntity::getExperimentInstanceId,experimentInstanceId)
                    .set(ExperimentInstanceEntity::getState, ExperimentStateEnum.ONGOING.getState())
                    .update();
            experimentParticipatorService.lambdaUpdate()
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId,experimentInstanceId)
                    .set(ExperimentParticipatorEntity::getState, ExperimentStateEnum.ONGOING.getState())
                    .update();

            ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.getById(experimentInstanceId);

            List<ExperimentSettingEntity> experimentSettingEntityList = experimentSettingService.lambdaQuery()
                    .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                    .list();
            ExperimentSettingEntity experimentSettingEntity1 = experimentSettingEntityList
                    .stream()
                    .filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class.getName()))
                    .findFirst()
                    .orElse(null);
            ExperimentSettingEntity experimentSettingEntity2 = experimentSettingEntityList
                    .stream()
                    .filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class.getName()))
                    .findFirst()
                    .orElse(null);
            *//**
             * 标准模式
             *//*
            if (experimentInstanceEntity.getModel().equals(ExperimentModeEnum.STANDARD.getCode())) {
                Assert.isNull(experimentSettingEntity1, "SchemeSetting not setting");
                Assert.isNull(experimentSettingEntity2, "SandSetting not setting");
                ExperimentSetting.SchemeSetting schemeSetting =
                        JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
                ExperimentSetting.SandSetting sandSetting =
                        JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SandSetting.class);



            }
            if (experimentInstanceEntity.getModel().equals(ExperimentModeEnum.SAND.getCode())) {
                ExperimentSetting.SandSetting sandSetting =
                        JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
                sandSetting.getInterval()

            }
            if (experimentInstanceEntity.getModel().equals(ExperimentModeEnum.SCHEME.getCode())) {
                ExperimentSetting.SchemeSetting schemeSetting =
                        JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
            }*/


        }
        return false;
    }

    /**
     * @param
     * @return
     * @说明: 根据小组ID获取小组信息
     * @关联表: experiment_group
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月13日 上午11:02:07
     */
    public ExperimentGroupResponse getGroupInfoByExperimentId(String experimentGroupId, String experimentGInstanceId) {
        ExperimentGroupEntity groupEntity = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentGInstanceId)
                .eq(ExperimentGroupEntity::getDeleted, false)
                .oneOpt().orElse(null);
        if (groupEntity == null) {
            throw new ExperimentException(ExperimentStatusCode.NO_EXIST_GROUP_ID);
        }
        ExperimentGroupResponse groupResponse = ExperimentGroupResponse.builder()
                .experimentGroupId(groupEntity.getExperimentGroupId())
                .experimentInstanceId(groupEntity.getExperimentInstanceId())
                .groupNo(groupEntity.getGroupNo())
                .groupName(groupEntity.getGroupName())
                .groupAlias(groupEntity.getGroupAlias())
                .groupState(groupEntity.getGroupState())
                .build();
        return groupResponse;
    }
}