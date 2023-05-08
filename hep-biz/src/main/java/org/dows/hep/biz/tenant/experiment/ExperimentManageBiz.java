package org.dows.hep.biz.tenant.experiment;

import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountGroupApi;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountUserApi;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountUserRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountUserResponse;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.tenant.experiment.request.GroupSettingRequest;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.dows.user.api.api.UserExtinfoApi;
import org.dows.user.api.api.UserInstanceApi;
import org.dows.user.api.request.UserExtinfoRequest;
import org.dows.user.api.request.UserInstanceRequest;
import org.dows.user.api.response.UserExtinfoResponse;
import org.dows.user.api.response.UserInstanceResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.dows.hep.biz.base.org.OrgBiz.createCode;
import static org.dows.hep.biz.base.org.OrgBiz.randomWord;

/**
 * @author lait.zhang
 * @description project descr:实验:实验管理
 * @date 2023年4月18日 上午10:45:07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentManageBiz {
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;
    // 实验设置
    private final ExperimentSettingService experimentSettingService;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验小组
    private final ExperimentGroupService experimentGroupService;
    private final IdGenerator idGenerator;
    private final AccountGroupApi accountGroupApi;
    private final ExperimentPersonService experimentPersonService;
    private final AccountUserApi accountUserApi;
    private final UserInstanceApi userInstanceApi;
    private final UserExtinfoApi userExtinfoApi;
    private final AccountInstanceApi accountInstanceApi;
    private final CasePersonService casePersonService;

//    private final

    /**
     * @param
     * @return
     * @说明: 分配实验
     * @关联表: ExperimentInstance, experimentSetting
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @DSTransactional
    public String allot(CreateExperimentRequest createExperiment) {
        ExperimentInstanceEntity experimentInstance = ExperimentInstanceEntity.builder()
                .experimentInstanceId(idGenerator.nextIdStr())
                .startTime(createExperiment.getStartTime())
                .experimentName(createExperiment.getExperimentName())
                .experimentDescr(createExperiment.getExperimentDescr())
                .model(createExperiment.getModel())
                .state(0)
                .caseInstanceId(createExperiment.getCaseInstanceId())
                .caseName(createExperiment.getCaseName())
                .build();
        // 保存实验实例
        experimentInstanceService.saveOrUpdate(experimentInstance);

        ExperimentSetting experimentSetting = createExperiment.getExperimentSetting();
        List<AccountInstanceResponse> teachers = createExperiment.getTeachers();
        List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
        for (AccountInstanceResponse instance : teachers) {
            ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                    .experimentParticipatorId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .accountId(instance.getAccountId())
                    .accountName(instance.getAccountName())
                    .participatorType(0)
                    .build();
            experimentParticipatorEntityList.add(experimentParticipatorEntity);
        }
        // 保存实验参与人
        experimentParticipatorService.saveOrUpdateBatch(experimentParticipatorEntityList);

        // 标准模式
        if (null != experimentSetting.getSchemeSetting() && null != experimentSetting.getSandSetting()) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSchemeSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSchemeSetting()))
                    .build();
            // 保存方案设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);

            experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSandSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSandSetting()))
                    .build();
            // 保存沙盘设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);
            // 沙盘模式
        } else if (null != experimentSetting.getSandSetting()) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSandSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSandSetting()))
                    .build();
            //保存沙盘设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);
            // 方案设计模式i
        } else if (null != experimentSetting.getSchemeSetting()) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSchemeSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSchemeSetting()))
                    .build();
            // 保存方案设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);
        }
        return experimentInstance.getExperimentInstanceId();
    }

    /**
     * @param
     * @return
     * @说明: 实验分组ss
     * @关联表: experimentGroup, experimentParticipator
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean grouping(GroupSettingRequest groupSetting) {

        ExperimentGroupEntity experimentGroupEntity = ExperimentGroupEntity.builder()
                .experimentGroupId(idGenerator.nextIdStr())
                .experimentInstanceId(groupSetting.getExperimentInstanceId())
                .groupAlias(groupSetting.getGroupAlias())
                .memberCount(groupSetting.getMemberCount())
                .groupNo(groupSetting.getGroupNo())
                .groupName(groupSetting.getGroupName())
                .groupAlias(groupSetting.getGroupAlias())
                .build();

        // 保存实验小组
        experimentGroupService.saveOrUpdate(experimentGroupEntity);
        //todo
        List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
        List<GroupSettingRequest.ExperimentParticipator> experimentParticipators = groupSetting.getExperimentParticipators();
        for (GroupSettingRequest.ExperimentParticipator experimentParticipator : experimentParticipators) {
            ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                    .id(Long.valueOf(experimentParticipator.getId()))
                    .experimentParticipatorId(idGenerator.nextIdStr())
                    .experimentInstanceId(groupSetting.getExperimentInstanceId())
                    .accountId(experimentParticipator.getParticipatorId())
                    .accountName(experimentParticipator.getParticipatorName())
                    .groupNo(groupSetting.getGroupNo())
                    .groupName(groupSetting.getGroupName())
                    .experimentGroupId(experimentGroupEntity.getExperimentGroupId())
                    .participatorType(2)
                    .build();
            // 如果是0【第一个人】设置为组长
            if (experimentParticipator.getSeq() == 0) {
                experimentParticipatorEntity.setParticipatorType(1);
            }
            experimentParticipatorEntityList.add(experimentParticipatorEntity);
        }
        // 保存实验参与人[学生]
        return experimentParticipatorService.saveOrUpdateBatch(experimentParticipatorEntityList);
    }

    /**
     * @param
     * @return
     * @说明: 获取实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<ExperimentListResponse> list() {


        return new ArrayList<ExperimentListResponse>();
    }


    /**
     * @param
     * @return
     * @说明: 分页实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        Page page = new Page<ExperimentInstanceEntity>();
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.addOrder(pageExperimentRequest.getDesc() ?
                OrderItem.desc(pageExperimentRequest.getOrderBy()) : OrderItem.asc(pageExperimentRequest.getOrderBy()));
        Page page1 = experimentInstanceService.page(page, experimentInstanceService.lambdaQuery()
                .likeLeft(ExperimentInstanceEntity::getExperimentName, pageExperimentRequest.getKeyword())
                .likeLeft(ExperimentInstanceEntity::getCaseName, pageExperimentRequest.getKeyword())
                .likeLeft(ExperimentInstanceEntity::getExperimentDescr, pageExperimentRequest.getKeyword()));
        return page1;
    }

    /**
     * @param
     * @return
     * @说明: 实验小组保存案例人物
     * @关联表: ExperimentPerson
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午11:23:07
     */
    @DSTransactional
    public Integer addExperimentGroupPerson(CreateExperimentRequest request) {
        Integer count = 0;
        List<AccountInstanceResponse> instanceResponses = request.getTeachers();
        //1、通过案例人物ID找到账户ID，进而找到姓名
        instanceResponses.forEach(instance -> {
            CasePersonEntity entity = casePersonService.lambdaQuery()
                    .eq(CasePersonEntity::getCaseOrgId, instance.getOrgId())
                    .eq(CasePersonEntity::getDeleted, false)
                    .eq(CasePersonEntity::getCasePersonId, instance.getAccountId())
                    .one();
            instance.setAccountName(accountInstanceApi.getAccountInstanceByAccountId(entity.getAccountId()).getAccountName());
        });
        //2、保存实验小组人物信息
        for (AccountInstanceResponse model : instanceResponses) {
            ExperimentPersonEntity entity = ExperimentPersonEntity.builder()
                    .experimentInstanceId(request.getExperimentInstanceId())
                    .experimentGroupId(request.getExperimentGroupId())
                    .experimentPersonId(idGenerator.nextIdStr())
                    .appId(request.getAppId())
                    .experimentOrgId(model.getOrgId())
                    .experimentOrgName(model.getOrgName())
                    .experimentAccountId(model.getAccountId())
                    .experimentAccountName(model.getAccountName())
                    .periods(request.getPeriods())
                    .build();
            experimentPersonService.save(entity);
            count++;
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 案例人物复制到实验
     * @关联表: case_person、experiment_person
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月06日 下午17:20:07
     */
    @DSTransactional
    public Boolean copyExperimentPerson(CreateExperimentRequest createExperiment) {
        //1、复制案例人物到每个实验，有几个实验小组就要分配几次人物
        List<ExperimentGroupEntity> entityList = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, createExperiment.getExperimentInstanceId())
                .eq(ExperimentGroupEntity::getDeleted, false)
                .list();
        List<AccountInstanceResponse> teachers = createExperiment.getTeachers();
        entityList.forEach(model -> {
            if (teachers != null && teachers.size() > 0) {
                teachers.forEach(teacher -> {
                    //1、案例人物复制一份到实验中
                    //1.1、获取用户信息及简介并创建新用户及简介
                    AccountUserResponse accountUser = accountUserApi.getUserByAccountId(teacher.getAccountId());
                    UserInstanceResponse userInstanceResponse = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
                    UserExtinfoResponse userExtinfoResponse = userExtinfoApi.getUserExtinfoByUserId(accountUser.getUserId());
                    UserInstanceRequest userInstanceRequest = new UserInstanceRequest();
                    BeanUtils.copyProperties(userInstanceResponse, userInstanceRequest, new String[]{"id", "accountId"});
                    String userId = userInstanceApi.insertUserInstance(userInstanceRequest);
                    UserExtinfoRequest userExtinfo = UserExtinfoRequest.builder()
                            .userId(userId)
                            .intro(userExtinfoResponse.getIntro())
                            .build();
                    String extinfoId = userExtinfoApi.insertUserExtinfo(userExtinfo);
                    //1.2、获取该账户的所有信息
                    AccountInstanceResponse accountInstanceResponse = accountInstanceApi.getAccountInstanceByAccountId(teacher.getAccountId());
                    //1.3、复制账户信息
                    AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                            .appId(accountInstanceResponse.getAppId())
                            .avatar(accountInstanceResponse.getAvatar())
                            .status(accountInstanceResponse.getStatus())
                            .source("机构人物")
                            .principalType(accountInstanceResponse.getPrincipalType())
                            .identifier(createCode(7))
                            .accountName(randomWord(6))
                            .build();
                    AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(accountInstanceRequest);
                    //1.4、创建账户和用户之间的关联关系
                    AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                            .accountId(vo.getAccountId())
                            .userId(userId)
                            .appId(accountInstanceResponse.getAppId())
                            .tentantId(accountInstanceResponse.getTenantId()).build();
                    this.accountUserApi.createAccountUser(accountUserRequest);
                    //2、添加新人物到实验中
                    ExperimentPersonEntity entity = ExperimentPersonEntity.builder()
                            .experimentPersonId(vo.getAccountId())
                            .experimentInstanceId(createExperiment.getExperimentInstanceId())
                            .experimentGroupId(model.getExperimentGroupId())
                            .experimentOrgId(teacher.getOrgId())
                            .experimentOrgName(teacher.getAccountId())
                            .experimentAccountId(teacher.getAccountName())
                            .experimentAccountName(teacher.getOrgName())
                            .build();
                    experimentPersonService.save(entity);
                });
            }
        });
        return true;
    }
}