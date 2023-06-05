package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.enums.EnumExperimentParticipator;
import org.dows.hep.api.exception.ExperimentParticipatorException;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.AllotActorRequest;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.request.ExperimentParticipatorRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.entity.ExperimentActorEntity;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final ExperimentInstanceService experimentInstanceService;

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
     * @关联表:experiment_group
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
    public List<ExperimentParticipatorResponse> listGroupMembers(String experimentGroupId, String experimentInstanceId) {
        List<ExperimentParticipatorEntity> entities = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentGroupId,experimentGroupId)
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId,experimentInstanceId)
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
    public List<ExperimentOrgEntity> listExperimentGroupOrg(String experimentGroupId, String experimentInstanceId, String periods) {
        List<ExperimentOrgEntity> entities = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentGroupId,experimentGroupId)
                .eq(ExperimentOrgEntity::getExperimentInstanceId,experimentInstanceId)
                .eq(ExperimentOrgEntity::getPeriods,periods)
                .eq(ExperimentOrgEntity::getDeleted,false)
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
    public Boolean allotGroupMembers(ExperimentParticipatorRequest request) {
        ExperimentParticipatorEntity model = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentParticipatorId,request.getExperimentParticipatorId())
                .eq(ExperimentParticipatorEntity::getAppId,request.getAppId())
                .eq(ExperimentParticipatorEntity::getExperimentGroupId,request.getExperimentGroupId())
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId,request.getExperimentInstanceId())
                .eq(ExperimentParticipatorEntity::getDeleted,false)
                .one();
        if(model == null || ReflectUtil.isObjectNull(model)){
            throw new ExperimentParticipatorException(EnumExperimentParticipator.PARTICIPATOR_NOT_EXIST_EXCEPTION);
        }
        ExperimentParticipatorEntity entity = ExperimentParticipatorEntity.builder()
                .experimentOrgIds(request.getExperimentOrgIds())
                .experimentOrgNames(request.getExperimentOrgNames())
                .participatorState(3)
                .id(model.getId())
                .build();
        return experimentParticipatorService.updateById(entity);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 获取该用户、该实验的方案设计 item
     * @date 2023/5/30 19:25
     */
    public List<ExperimentSchemeItemResponse> listExperimentScheme(String experimentInstanceId) {
        if (StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

//        ExperimentInstanceEntity instance = experimentInstanceService.lambdaQuery()
//                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
//                .one();
//        if (BeanUtil.isEmpty(instance)) {
//            throw new BizException(ExperimentESCEnum.DATA_NULL);
//        }
//
//        // 获取方案设计Item 列表
//        String caseInstanceId = instance.getCaseInstanceId();
//        CaseSchemeResponse caseSchemeResponse = userCaseSchemeBiz.getCaseSchemeOfCaseInstance(caseInstanceId);
//        if (BeanUtil.isEmpty(caseSchemeResponse)) {
//            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
//        }
//
//        return convertCSR2ESR(caseSchemeResponse);
        return null;
    }

    private List<ExperimentSchemeItemResponse> convertCSR2ESR(CaseSchemeResponse caseSchemeResponse) {
        List<QuestionSectionItemResponse> sectionItemList = caseSchemeResponse.getSectionItemList();
        if (CollUtil.isEmpty(sectionItemList)) {
            return new ArrayList<>();
        }

        ArrayList<ExperimentSchemeItemResponse> result = new ArrayList<>();
        sectionItemList.forEach(sectionItem -> {
            QuestionResponse question = sectionItem.getQuestion();
            String questionSectionItemId = sectionItem.getQuestionSectionItemId();
            ExperimentSchemeItemResponse itemResponse = buildItemResponse(question, questionSectionItemId);
            if (BeanUtil.isNotEmpty(itemResponse)) {
                result.add(itemResponse);
            }
        });

        // set video-question
        Integer containsVideo = caseSchemeResponse.getContainsVideo();
        if (Objects.nonNull(containsVideo) && containsVideo == 1) {
            ExperimentSchemeItemResponse itemResponse = new ExperimentSchemeItemResponse();
            itemResponse.setQuestionSectionItemId("1008610010");
            itemResponse.setQuestionTitle("上传视频");
            result.add(itemResponse);
        }

        return result;
    }

    private ExperimentSchemeItemResponse buildItemResponse(QuestionResponse question, String questionSectionItemId) {
        // 判空
        if (BeanUtil.isEmpty(question)) {
            return null;
        }

        // 处理当前结点
        String questionTitle = question.getQuestionTitle();
        ExperimentSchemeItemResponse result = new ExperimentSchemeItemResponse();
        result.setQuestionSectionItemId(questionSectionItemId);
        result.setQuestionTitle(questionTitle);


        // 是否有子类
        List<QuestionResponse> children = question.getChildren();
        if (CollUtil.isEmpty(children)) {
            return result;
        }

        // 处理子类
        List<ExperimentSchemeItemResponse> itemList = new ArrayList<>();
        children.forEach(questionResponse -> {
            ExperimentSchemeItemResponse itemResponse = buildItemResponse(questionResponse, "");
            itemList.add(itemResponse);
        });
        result.setChildren(itemList);

        return result;
    }

}