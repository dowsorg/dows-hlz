package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.enums.EnumExperimentParticipator;
import org.dows.hep.api.enums.ExperimentStatusCode;
import org.dows.hep.api.enums.ParticipatorTypeEnum;
import org.dows.hep.api.event.GroupMemberAllotEvent;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.exception.ExperimentParticipatorException;
import org.dows.hep.api.user.experiment.request.AllotActorRequest;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.entity.ExperimentActorEntity;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentActorService;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentOrgService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private final ExperimentActorService experimentActorService;

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
        // 更新组名和状态
        return experimentGroupService.lambdaUpdate()
                .eq(ExperimentGroupEntity::getExperimentGroupId, createGroup.getExperimentGroupId())
                .eq(ExperimentGroupEntity::getExperimentInstanceId, createGroup.getExperimentInstanceId())
                .update(ExperimentGroupEntity.builder()
                        .groupState(EnumExperimentGroupStatus.ASSIGN_FUNC.getCode())
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
        List<ExperimentParticipatorEntity> entityList = new ArrayList<>();
        participatorList.forEach(request -> {
            ExperimentParticipatorEntity model = experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getExperimentParticipatorId, request.getExperimentParticipatorId())
                    .eq(ExperimentParticipatorEntity::getAppId, request.getAppId())
                    .eq(ExperimentParticipatorEntity::getExperimentGroupId, request.getExperimentGroupId())
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                    .eq(ExperimentParticipatorEntity::getDeleted, false)
                    .one();
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
            //1、更新组状态为等待其他小组分配完成
            experimentGroupService.lambdaUpdate()
                    .eq(ExperimentGroupEntity::getExperimentGroupId, request.getExperimentGroupId())
                    .eq(ExperimentGroupEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                    .update(ExperimentGroupEntity.builder()
                            .groupState(EnumExperimentGroupStatus.WAIT_ALL_GROUP_ASSIGN.getCode())
                            .build());
        });

        // todo 发布事件，计数小组是否分配到齐，是否都分配好
        applicationEventPublisher.publishEvent(new GroupMemberAllotEvent(participatorList));

        return experimentParticipatorService.updateBatchById(entityList);
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