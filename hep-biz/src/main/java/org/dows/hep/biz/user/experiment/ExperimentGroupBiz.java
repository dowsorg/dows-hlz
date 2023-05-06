package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.AllotActorRequest;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.entity.ExperimentActorEntity;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentActorService;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.sequence.api.IdGenerator;
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
    public Boolean createGroup(CreateGroupRequest createGroup) {

        ExperimentGroupEntity experimentGroupEntity = ExperimentGroupEntity.builder()
                .groupName(createGroup.getGroupName())
                .build();
        boolean update = experimentGroupService.lambdaUpdate()
                .eq(ExperimentGroupEntity::getExperimentGroupId, createGroup.getExperimentGroupId())
                .eq(ExperimentGroupEntity::getExperimentInstanceId, createGroup.getExperimentInstanceId())
                .update(experimentGroupEntity);
        return update;
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
        allotActorRequest.getActorOrgMap().forEach((accountInfo,orgs)->{
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
                if(1==1){
//                    experimentActorEntity.setActorId(org.getPrincipalId());
//                    experimentActorEntity.setActorType();
                }
                // 如果是[0:教师，1:组长，2：学生]
                if(1==1){
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
     * @关联表:
     * @工时: 0.5H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<ExperimentGroupResponse> listGroup(String experimentInstanceId) {
        List<ExperimentGroupEntity> entities = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId,experimentInstanceId)
                .eq(ExperimentGroupEntity::getDeleted,false)
                .list();
        //复制属性
        List<ExperimentGroupResponse> experimentGroupResponse = new ArrayList<>();
        if(entities != null && entities.size() > 0){
            entities.forEach(entity->{
                ExperimentGroupResponse response = new ExperimentGroupResponse();
                BeanUtil.copyProperties(entity,response);
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
    public List<ExperimentParticipatorResponse> listGroupMembers(String experimentGroupId) {
        List<ExperimentParticipatorEntity> entities = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentGroupId,experimentGroupId)
                .eq(ExperimentParticipatorEntity::getDeleted,false)
                .list();
        //复制属性
        List<ExperimentParticipatorResponse> experimentParticipatorResponses = new ArrayList<>();
        if(entities != null && entities.size() > 0){
            entities.forEach(entity->{
                ExperimentParticipatorResponse response = new ExperimentParticipatorResponse();
                BeanUtil.copyProperties(entity,response);
                experimentParticipatorResponses.add(response);
            });
        }
        return experimentParticipatorResponses;
    }
}