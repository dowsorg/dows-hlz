package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.*;
import org.dows.account.request.*;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgGeoResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.account.response.AccountUserResponse;
import org.dows.framework.crud.api.model.PageInfo;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.EnumExperimentParticipator;
import org.dows.hep.api.exception.ExperimentParticipatorException;
import org.dows.hep.api.tenant.experiment.request.*;
import org.dows.hep.api.core.CreateExperimentForm;
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

import java.util.*;
import java.util.stream.Collectors;

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
    private final ExperimentOrgService experimentOrgService;
    private final CaseOrgService caseOrgService;
    private final AccountOrgApi accountOrgApi;
    private final AccountOrgGeoApi accountOrgGeoApi;
    private final CaseOrgFeeService caseOrgFeeService;

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
     * 回填表单
     *
     * @return
     */
    public CreateExperimentForm getAllotData(String experimentInstanceId, String appId) {
        CreateExperimentForm createExperimentForm = new CreateExperimentForm();

        ExperimentInstanceEntity experimentInstance = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .last("limit 1")
                .getEntity();

        ExperimentParticipatorEntity experimentParticipator = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                // todo 查找老师,后面定义为枚举，这里先实现
                .eq(ExperimentParticipatorEntity::getParticipatorType, 0)
                .last("limit 1")
                .getEntity();

        List<ExperimentSettingEntity> experimentSettings = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentSettingEntity::getAppId, appId)
                .list();

        // 处理实验
        BeanUtil.copyProperties(experimentInstance, createExperimentForm, "teachers", "experimentSetting");
        // 处理老师
        AccountInstanceResponse accountInstanceResponse = new AccountInstanceResponse();
        BeanUtil.copyProperties(experimentParticipator, accountInstanceResponse);
        // todo 一个实验是否可以有多个老师
        List<AccountInstanceResponse> teachers = Arrays.asList(accountInstanceResponse);
        createExperimentForm.setTeachers(teachers);
        // 处理实验设置
        ExperimentSetting experimentSetting = new ExperimentSetting();
        for (ExperimentSettingEntity expSetting : experimentSettings) {
            String configKey = expSetting.getConfigKey();
            if (configKey.equals(ExperimentSetting.SandSetting.class.getName())) {
                ExperimentSetting.SandSetting bean = JSONUtil.toBean(expSetting.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
                experimentSetting.setSandSetting(bean);
            }
            if (configKey.equals(ExperimentSetting.SchemeSetting.class.getName())) {
                ExperimentSetting.SchemeSetting bean = JSONUtil.toBean(expSetting.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
                experimentSetting.setSchemeSetting(bean);
            }
        }
        createExperimentForm.setExperimentSetting(experimentSetting);
        return createExperimentForm;
    }

    /**
     * @param
     * @return
     * @说明: 实验分组
     * @关联表: experimentGroup, experimentParticipator
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @DSTransactional
    public Boolean grouping(ExperimentGroupSettingRequest experimentGroupSettingRequest, String caseInstanceId) {
        List<ExperimentGroupSettingRequest.GroupSetting> experimentGroupSettings = experimentGroupSettingRequest.getGroupSettings();
        List<ExperimentGroupEntity> experimentGroupEntitys = new ArrayList<>();
        Map<String, List<ExperimentParticipatorEntity>> groupParticipators = new HashMap<>();
        for (ExperimentGroupSettingRequest.GroupSetting groupSetting : experimentGroupSettings) {
            ExperimentGroupEntity experimentGroupEntity = ExperimentGroupEntity.builder()
                    .appId(groupSetting.getAppId())
                    .experimentGroupId(idGenerator.nextIdStr())
                    .experimentInstanceId(groupSetting.getExperimentInstanceId())
                    .groupAlias(groupSetting.getGroupAlias())
                    .memberCount(groupSetting.getMemberCount())
                    .groupNo(groupSetting.getGroupNo())
                    .groupName(groupSetting.getGroupName())
                    .build();
            experimentGroupEntitys.add(experimentGroupEntity);


            //todo
            List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
            List<ExperimentGroupSettingRequest.ExperimentParticipator> experimentParticipators = groupSetting.getExperimentParticipators();
            for (ExperimentGroupSettingRequest.ExperimentParticipator experimentParticipator : experimentParticipators) {
                ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                        .experimentParticipatorId(idGenerator.nextIdStr())
                        .appId(groupSetting.getAppId())
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
                // 记录每组对应的组员
                groupParticipators.put(experimentGroupEntity.getExperimentGroupId(), experimentParticipatorEntityList);
            }
        }


        // 判断实验参与人数是否大于该案例得机构数
        List<CaseOrgEntity> entityList = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseInstanceId, caseInstanceId)
                .eq(CaseOrgEntity::getDeleted, false)
                .list();
        List<ExperimentParticipatorEntity> collect = groupParticipators.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList());
        if (collect.size() <= entityList.size()) {
            throw new ExperimentParticipatorException(EnumExperimentParticipator.PARTICIPATOR_NUMBER_CANNOT_MORE_THAN_ORG_EXCEPTION);
        }
        // 保存实验小组
        experimentGroupService.saveOrUpdateBatch(experimentGroupEntitys);
        // 保存实验参与人[学生]
        experimentParticipatorService.saveOrUpdateBatch(collect);
        return true;
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
    public List<ExperimentListResponse> list(ExperimentQueryRequest experimentQueryRequest) {
        List<ExperimentInstanceEntity> list = experimentInstanceService.lambdaQuery()
                .likeLeft(ExperimentInstanceEntity::getExperimentName, experimentQueryRequest.getExperimentName())
                .likeLeft(ExperimentInstanceEntity::getCaseName, experimentQueryRequest.getCaseNaem()).list();
        return BeanConvert.beanConvert(list, ExperimentListResponse.class);
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
    public PageInfo<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        Page page = new Page<ExperimentInstanceEntity>();
        page.setSize(pageExperimentRequest.getPageSize());
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.addOrder(pageExperimentRequest.getDesc() ?
                OrderItem.desc(pageExperimentRequest.getOrderBy()) : OrderItem.asc(pageExperimentRequest.getOrderBy()));
        page = experimentInstanceService.page(page, experimentInstanceService.lambdaQuery()
                .likeLeft(ExperimentInstanceEntity::getExperimentName, pageExperimentRequest.getKeyword())
                .likeLeft(ExperimentInstanceEntity::getCaseName, pageExperimentRequest.getKeyword())
                .likeLeft(ExperimentInstanceEntity::getExperimentDescr, pageExperimentRequest.getKeyword()));
        PageInfo pageInfo = experimentInstanceService.getPageInfo(page, ExperimentListResponse.class);
        return pageInfo;
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
    public Boolean addExperimentGroupPerson(CreateExperimentRequest request) {
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
        List<ExperimentPersonEntity> entityList = new ArrayList<>();
        for (AccountInstanceResponse model : instanceResponses) {
            ExperimentPersonEntity entity = ExperimentPersonEntity.builder()
                    .experimentInstanceId(request.getExperimentInstanceId())
                    .experimentGroupId(request.getExperimentGroupId())
                    .experimentPersonId(idGenerator.nextIdStr())
                    .appId(request.getAppId())
                    .experimentOrgId(model.getOrgId())
                    .experimentOrgName(model.getOrgName())
                    .accountId(model.getAccountId())
                    .accountName(model.getAccountName())
                    .periods(request.getPeriods())
                    .build();
            entityList.add(entity);
        }
        return experimentPersonService.saveOrUpdateBatch(entityList);
    }

    /**
     * @param
     * @return
     * @说明: 案例人物复制到实验
     * @关联表: case_person、experiment_person、case_org、account_org、account_org_geo、account_org_info
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月09日 上午10:20:07
     */
    @DSTransactional
    public Boolean copyExperimentPersonAndOrg(CreateExperimentRequest createExperiment) {
        Map<String, Object> map = new HashMap<>();
        //1、复制案例人物到每个实验，有几个实验小组就要分配几次人物和机构
        List<ExperimentGroupEntity> entityList = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, createExperiment.getExperimentInstanceId())
                .eq(ExperimentGroupEntity::getDeleted, false)
                .list();
        List<AccountInstanceResponse> teachers = createExperiment.getTeachers();
        entityList.forEach(model -> {
            if (teachers != null && teachers.size() > 0) {
                //1.1、根据案例机构复制案例机构
                CaseOrgEntity orgEntity = caseOrgService.lambdaQuery()
                        .eq(CaseOrgEntity::getCaseOrgId, createExperiment.getCaseOrgId())
                        .eq(CaseOrgEntity::getDeleted, false)
                        .one();
                AccountOrgResponse orgResponse = accountOrgApi.getAccountOrgByOrgId(orgEntity.getOrgId(), createExperiment.getAppId());
                AccountOrgGeoResponse orgGeoResponse = accountOrgGeoApi.getAccountOrgInfoByOrgId(orgEntity.getOrgId());
                //1.1.1、生成随机code，复制机构基础信息
                String orgCode = createCode(7);
                AccountOrgRequest request = new AccountOrgRequest();
                BeanUtil.copyProperties(orgResponse, request, new String[]{"id", "dt"});
                request.setOrgCode(orgCode);
                request.setOperationManual(orgEntity.getHandbook());
                String orgId = accountOrgApi.createAccountOrg(request);
                //1.1.2. 创建案例机构实例副本
                String experimentOrgId = idGenerator.nextIdStr();
                ExperimentOrgEntity entity = ExperimentOrgEntity.builder()
                        .experimentOrgId(experimentOrgId)
                        .orgId(orgId)
                        .appId(createExperiment.getAppId())
                        .experimentOrgName(orgEntity.getOrgName())
                        .experimentInstanceId(orgEntity.getCaseInstanceId())
                        .experimentGroupId(model.getExperimentGroupId())
                        .caseOrgId(orgEntity.getCaseOrgId())
                        .caseOrgName(orgEntity.getOrgName())
                        .periods(createExperiment.getPeriods())
                        .build();
                experimentOrgService.save(entity);
                //1.1.3、创建机构费用明细副本
                List<CaseOrgFeeEntity> caseOrgList = caseOrgFeeService
                        .lambdaQuery()
                        .eq(CaseOrgFeeEntity::getCaseOrgId, createExperiment.getCaseOrgId())
                        .eq(CaseOrgFeeEntity::getDeleted, false)
                        .list();
                List<CaseOrgFeeEntity> feeList = new ArrayList<>();
                caseOrgList.forEach(fee -> {
                    CaseOrgFeeEntity feeEntity = CaseOrgFeeEntity
                            .builder()
                            .caseOrgFeeId(idGenerator.nextIdStr())
                            .caseOrgIndicatorId(fee.getCaseOrgIndicatorId())
                            .caseInstanceId(fee.getCaseInstanceId())
                            .caseOrgId(orgId)
                            .orgFunctionId(fee.getOrgFunctionId())
                            .functionName(fee.getFunctionName())
                            .reimburseRatio(fee.getReimburseRatio())
                            .fee(fee.getFee())
                            .feeCode(fee.getFeeCode())
                            .feeName(fee.getFeeName())
                            .appId(fee.getAppId())
                            .ver(fee.getVer())
                            .caseIdentifier(fee.getCaseIdentifier())
                            .build();
                    feeList.add(feeEntity);
                });
                caseOrgFeeService.saveBatch(feeList);
                //1.1.4、创建机构点位
                AccountOrgGeoRequest geoRequest = AccountOrgGeoRequest
                        .builder()
                        .orgId(orgId)
                        .orgName(request.getOrgName())
                        .orgLongitude(orgGeoResponse.getOrgLongitude())
                        .orgLatitude(orgGeoResponse.getOrgLatitude())
                        .build();
                accountOrgGeoApi.insertOrgGeo(geoRequest);
                Set<String> experimentAccountIds = new HashSet<>();
                for (AccountInstanceResponse teacher : teachers) {
                    //2、案例人物复制一份到实验中
                    //2.1、获取用户信息及简介并创建新用户及简介
                    AccountUserResponse accountUser = accountUserApi.getUserByAccountId(teacher.getAccountId());
                    UserInstanceResponse userInstanceResponse = userInstanceApi.getUserInstanceByUserId(accountUser.getUserId());
                    UserExtinfoResponse userExtinfoResponse = userExtinfoApi.getUserExtinfoByUserId(accountUser.getUserId());
                    UserInstanceRequest userInstanceRequest = new UserInstanceRequest();
                    BeanUtils.copyProperties(userInstanceResponse, userInstanceRequest, new String[]{"id", "accountId", "dt"});
                    String userId = userInstanceApi.insertUserInstance(userInstanceRequest);
                    UserExtinfoRequest userExtinfo = UserExtinfoRequest.builder()
                            .userId(userId)
                            .intro(userExtinfoResponse.getIntro())
                            .build();
                    String extinfoId = userExtinfoApi.insertUserExtinfo(userExtinfo);
                    //2.2、获取该账户的所有信息
                    AccountInstanceResponse accountInstanceResponse = accountInstanceApi.getAccountInstanceByAccountId(teacher.getAccountId());
                    //2.3、复制账户信息
                    AccountInstanceRequest accountInstanceRequest = AccountInstanceRequest.builder()
                            .appId(accountInstanceResponse.getAppId())
                            .avatar(accountInstanceResponse.getAvatar())
                            .status(accountInstanceResponse.getStatus())
                            .source("实验人物")
                            .principalType(accountInstanceResponse.getPrincipalType())
                            .identifier(createCode(7))
                            .accountName(randomWord(6))
                            .build();
                    AccountInstanceResponse vo = accountInstanceApi.createAccountInstance(accountInstanceRequest);
                    //2.4、创建账户和用户之间的关联关系
                    AccountUserRequest accountUserRequest = AccountUserRequest.builder()
                            .accountId(vo.getAccountId())
                            .userId(userId)
                            .appId(accountInstanceResponse.getAppId())
                            .tentantId(accountInstanceResponse.getTenantId()).build();
                    this.accountUserApi.createAccountUser(accountUserRequest);
                    //2.5、获取案例人物ID
                    CasePersonEntity personEntity = casePersonService
                            .lambdaQuery()
                            .eq(CasePersonEntity::getCaseOrgId, createExperiment.getCaseOrgId())
                            .eq(CasePersonEntity::getAccountId, teacher.getAccountId())
                            .eq(CasePersonEntity::getDeleted, false)
                            .one();
                    //2.6、添加新人物到实验中
                    ExperimentPersonEntity entity1 = ExperimentPersonEntity.builder()
                            .experimentPersonId(idGenerator.nextIdStr())
                            .experimentInstanceId(createExperiment.getExperimentInstanceId())
                            .experimentGroupId(model.getExperimentGroupId())
                            .experimentOrgId(experimentOrgId)
                            .appId(createExperiment.getAppId())
                            .experimentOrgName(request.getOrgName())
                            .accountId(vo.getAccountId())
                            .accountName(vo.getAccountName())
                            .casePersonId(personEntity.getCasePersonId())
                            .build();
                    experimentPersonService.save(entity1);
                    experimentAccountIds.add(vo.getAccountId());
                }
                //2.7、复制人物到新建的小组
                experimentAccountIds.forEach(accountId -> {
                    AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(accountId);
                    AccountGroupRequest request1 = AccountGroupRequest
                            .builder()
                            .orgId(orgId)
                            .orgName(orgEntity.getOrgName())
                            .accountId(accountId)
                            .accountName(instanceResponse.getAccountName())
                            .userId(instanceResponse.getUserId())
                            .appId(createExperiment.getAppId())
                            .build();
                    accountGroupApi.insertAccountGroupNotCheck(request1);
                });
                experimentAccountIds = new HashSet<>();
            }
        });
        return true;
    }
}