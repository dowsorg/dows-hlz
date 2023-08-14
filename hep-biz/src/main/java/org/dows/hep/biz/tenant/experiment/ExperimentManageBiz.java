package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.*;
import org.dows.account.request.*;
import org.dows.account.response.*;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.core.CreateExperimentForm;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumParticipatorType;
import org.dows.hep.api.event.ExperimentEvent;
import org.dows.hep.api.event.InitializeEvent;
import org.dows.hep.api.event.StartEvent;
import org.dows.hep.api.event.SuspendEvent;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.*;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.response.ExperimentStateResponse;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.hep.biz.util.PeriodsTimerUtil;
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
import org.springframework.context.ApplicationEventPublisher;
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
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;
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
    // 事件发布
    private final ApplicationEventPublisher applicationEventPublisher;

    private final AccountRoleApi accountRoleApi;


    private final PersonManageBiz personManageBiz;

    private final ExperimentTaskScheduleService experimentTaskScheduleService;

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
    public String allot(CreateExperimentRequest createExperiment, String accountId) {
        // 获取参与教师
        List<AccountInstanceResponse> teachers = createExperiment.getTeachers();
        Set<String> teacherIds = new HashSet<>();
        teachers.forEach(teacher -> {
            teacherIds.add(teacher.getAccountId());
        });
        // 根据登录ID获取账户名和用户名
        AccountInstanceResponse instanceResponse = personManageBiz.getPersonalInformation(accountId, createExperiment.getAppId());
        // 填充数据
        ExperimentInstanceEntity experimentInstance = ExperimentInstanceEntity.builder()
                .appId(createExperiment.getAppId())
                .experimentInstanceId(idGenerator.nextIdStr())
                .startTime(createExperiment.getStartTime())
                .experimentName(createExperiment.getExperimentName())
                .experimentDescr(createExperiment.getExperimentDescr())
                .model(createExperiment.getModel())
                .state(EnumExperimentState.UNBEGIN.getState())
                .accountId(accountId)
                .appointor(instanceResponse.getAccountName())
                .experimentParticipatorIds(String.join(",", teacherIds))
                .caseInstanceId(createExperiment.getCaseInstanceId())
                .caseName(createExperiment.getCaseName())
                .casePic(createExperiment.getCasePic())
                .appointorName(instanceResponse.getUserName())
                .build();
        // 保存实验实例
        experimentInstanceService.save(experimentInstance);

        ExperimentSetting experimentSetting = createExperiment.getExperimentSetting();
        List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
        for (AccountInstanceResponse instance : teachers) {
            ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                    .experimentParticipatorId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .caseInstanceId(createExperiment.getCaseInstanceId())
                    .experimentStartTime(createExperiment.getStartTime())
                    .experimentName(experimentInstance.getExperimentName())
                    .accountId(instance.getAccountId())
                    .accountName(instance.getAccountName())
                    .state(EnumExperimentState.UNBEGIN.getState())
                    .model(experimentInstance.getModel())
                    .participatorType(EnumParticipatorType.TEACHER.getCode())
                    .build();
            experimentParticipatorEntityList.add(experimentParticipatorEntity);
        }
        // 保存实验参与人(教师/实验指导员等)
        experimentParticipatorService.saveBatch(experimentParticipatorEntityList);

        ExperimentSetting.SchemeSetting schemeSetting = experimentSetting.getSchemeSetting();
        ExperimentSetting.SandSetting sandSetting = experimentSetting.getSandSetting();
        List<ExperimentTimerEntity> experimentTimerEntities = new ArrayList<>();
        // 标准模式
        if (null != schemeSetting && null != sandSetting) {
            // 验证时间
            schemeSetting.validateTime(createExperiment.getStartTime());
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSchemeSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSchemeSetting()))
                    .build();

            // 保存方案设计
            experimentSettingService.save(experimentSettingEntity);

            experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSandSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSandSetting()))
                    .build();
            // 保存沙盘设计
            experimentSettingService.save(experimentSettingEntity);
            // 设置实验计时器
            PeriodsTimerUtil.buildPeriods(experimentInstance, experimentSetting, experimentTimerEntities, idGenerator);
            // 沙盘模式
        } else if (null != sandSetting) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSandSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSandSetting()))
                    .build();

            //保存沙盘设计
            experimentSettingService.save(experimentSettingEntity);
            // 设置实验计时器
            PeriodsTimerUtil.buildPeriods(experimentInstance, experimentSetting, experimentTimerEntities, idGenerator);
            // 方案设计模式
        } else if (null != schemeSetting) {
            // 验证时间
            schemeSetting.validateTime(createExperiment.getStartTime());
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSchemeSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSchemeSetting()))
                    .build();
            // 保存方案设计
            experimentSettingService.save(experimentSettingEntity);
            // 设置实验计时器
//            buildPeriods(experimentInstance, experimentSetting, experimentTimerEntities);
        }
        // 保存实验计时器
        experimentTimerService.saveBatch(experimentTimerEntities);
        return experimentInstance.getExperimentInstanceId();
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
                .eq(ExperimentParticipatorEntity::getCaseInstanceId,experimentGroupSettingRequest.getCaseInstanceId())
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
        applicationEventPublisher.publishEvent(new InitializeEvent(experimentGroupSettingRequest));

        return true;
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
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));

        List<ExperimentParticipatorEntity> experimentParticipatorList = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getParticipatorType, 0)
                .list();

        List<ExperimentSettingEntity> experimentSettings = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();

        // 处理实验
        BeanUtil.copyProperties(experimentInstance, createExperimentForm, "teachers", "experimentSetting");
        // 处理老师
        List<AccountInstanceResponse> accountInstanceResponseList = new ArrayList<>();
        experimentParticipatorList.forEach(experimentParticipator -> {
            AccountInstanceResponse accountInstanceResponse = new AccountInstanceResponse();
            BeanUtil.copyProperties(experimentParticipator, accountInstanceResponse);
            accountInstanceResponseList.add(accountInstanceResponse);
        });
        // todo 一个实验是否可以有多个老师
//        List<AccountInstanceResponse> teachers = Arrays.asList(accountInstanceResponse);
        createExperimentForm.setTeachers(accountInstanceResponseList);
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
     * 获取实验开始或暂停状态
     *
     * @return
     */
    public ExperimentStateResponse getExperimentState(String appId, String experimentInstanceId) {

        ExperimentStateResponse experimentStateResponse = new ExperimentStateResponse();

        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentInstanceEntity::getAppId, appId)
                //.ge(ExperimentInstanceEntity::getStartTime, LocalDateTime.now())
                .oneOpt()
                .orElse(null);
        if (experimentInstanceEntity == null) {
            throw new ExperimentException("不存在的实验!");
        }
        List<ExperimentSettingEntity> list = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        ExperimentSettingEntity experimentSettingEntity1 = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SchemeSetting.class.getName()))
                .findFirst()
                .orElse(null);
        ExperimentSettingEntity experimentSettingEntity2 = list.stream()
                .filter(e -> e.getConfigKey().equals(ExperimentSetting.SandSetting.class.getName()))
                .findFirst()
                .orElse(null);
        /**
         * 标准模式
         */
        /*if (experimentInstanceEntity.getModel().equals(ExperimentModeEnum.STANDARD.getCode())) {
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
        }
        if (experimentInstanceEntity.getModel().equals(ExperimentModeEnum.SCHEME.getCode())) {
            ExperimentSetting.SchemeSetting schemeSetting =
                    JSONUtil.toBean(experimentSettingEntity1.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
        }*/
        EnumExperimentState enumExperimentState = Arrays.stream(EnumExperimentState.values())
                .filter(e -> e.getState() == experimentInstanceEntity.getState())
                .findFirst().orElse(null);


        /**
         * todo 此处为兜底，是否需要这样做？
         */
        // 计算实验开始时间
        /*Date startTime = experimentInstanceEntity.getStartTime();
        int compare = DateUtil.compare(DateUtil.date(), startTime);
        // 实验已经开始
        if (compare > 0) {
            // 更新实验、参与者状态为准备中
            experimentInstanceService.lambdaUpdate()
                    .eq(ExperimentInstanceEntity::getExperimentInstanceId,experimentInstanceId)
                    .set(ExperimentInstanceEntity::getState, ExperimentStateEnum.PREPARE.getState())
                    .update();
            experimentParticipatorService.lambdaUpdate()
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId,experimentInstanceId)
                    .set(ExperimentParticipatorEntity::getState, ExperimentStateEnum.PREPARE.getState())
                    .update();
        }
        if (experimentStateEnum == ExperimentStateEnum.PREPARE) {
            ExperimentRestartRequest experimentRestartRequest = new ExperimentRestartRequest();
            experimentRestartRequest.setExperimentInstanceId(experimentInstanceId);
            experimentRestartRequest.setPaused(true);
            experimentRestartRequest.setModel(experimentInstanceEntity.getModel());
            experimentRestartRequest.setAppId(appId);
            experimentRestartRequest.setCurrentTime(new Date());
            applicationEventPublisher.publishEvent(new SuspendEvent(experimentRestartRequest));
        }*/
        //experimentInstanceEntity.setState(ExperimentStateEnum.SUSPEND.getState());
//        experimentInstanceService.lambdaUpdate().update(experimentInstanceEntity);
        experimentStateResponse.setEnumExperimentState(enumExperimentState);
        experimentStateResponse.setExperimentInstanceId(experimentInstanceEntity.getExperimentInstanceId());
        experimentStateResponse.setExperimentStartTime(experimentInstanceEntity.getStartTime());
        // todo 查询实验开始或暂停或结束,可直接差数据库
//        return experimentStateEnum;
        return experimentStateResponse;
    }


    /**
     * 更新实验状态
     */
    public void updateExperimentState(String experimentInstanceId) {


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
    public PageResponse<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        Page page = new Page<ExperimentInstanceEntity>();
        page.setSize(pageExperimentRequest.getPageSize());
        page.setCurrent(pageExperimentRequest.getPageNo());
        if (pageExperimentRequest.getOrder() != null) {
            String[] array = (String[]) pageExperimentRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageExperimentRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }
        try {
            if (!StrUtil.isBlank(pageExperimentRequest.getKeyword())) {
                page = experimentInstanceService.page(page, experimentInstanceService.lambdaQuery()
                        .likeLeft(ExperimentInstanceEntity::getExperimentName, pageExperimentRequest.getKeyword())
                        .likeLeft(ExperimentInstanceEntity::getCaseName, pageExperimentRequest.getKeyword())
                        .likeLeft(ExperimentInstanceEntity::getExperimentDescr, pageExperimentRequest.getKeyword())
                        .getWrapper());
            } else {
                page = experimentInstanceService.page(page, experimentInstanceService.lambdaQuery().getWrapper());
            }
        } catch (Exception e) {
            throw new ExperimentException(e.getCause().getMessage());
        }
        PageResponse pageInfo = experimentInstanceService.getPageInfo(page, ExperimentListResponse.class);
        return pageInfo;
    }

    public boolean delete(DeleteExperimentRequest deleteExperimentRequest) {
        List<String> experimentInstanceIds = deleteExperimentRequest.getExperimentInstanceId();
        boolean update = experimentInstanceService.lambdaUpdate()
                .in(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceIds)
                //.eq(ExperimentInstanceEntity::getExperimentInstanceId, pageExperimentRequest.getExperimentInstanceId())
                .set(ExperimentInstanceEntity::getDeleted, Boolean.TRUE)
                .update();
        //删除任务中的实验
        experimentTaskScheduleService.lambdaUpdate()
                .in(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceIds)
                .set(ExperimentTaskScheduleEntity::getDeleted, Boolean.TRUE)
                .update();
        return update;
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
    public Boolean copyExperimentPersonAndOrg(List<CreateExperimentRequest> createExperimentList) {
        createExperimentList.forEach(createExperiment -> {
            Map<String, Object> map = new HashMap<>();
            //1、复制案例人物到每个实验，有几个实验小组就要分配几次人物和机构
            List<ExperimentGroupEntity> entityList = experimentGroupService.lambdaQuery()
                    .eq(ExperimentGroupEntity::getExperimentInstanceId, createExperiment.getExperimentInstanceId())
                    .eq(ExperimentGroupEntity::getDeleted, false)
                    .list();
            List<AccountInstanceResponse> teachers = createExperiment.getTeachers();
            entityList.forEach(model -> {
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
                        .experimentInstanceId(createExperiment.getExperimentInstanceId())
                        .experimentGroupId(model.getExperimentGroupId())
                        .caseOrgId(orgEntity.getCaseOrgId())
                        .caseOrgName(orgEntity.getOrgName())
                        .handbook(orgEntity.getHandbook())
//                            .periods(createExperiment.getPeriods())
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
                //1、虚拟人物不为空则复制
                if (teachers != null && teachers.size() > 0) {
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
                                .userName(userInstanceResponse.getName())
                                .casePersonId(personEntity.getCasePersonId())
                                .build();
                        experimentPersonService.save(entity1);
                        experimentAccountIds.add(vo.getAccountId());
                    }
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
            });
        });
        return true;
    }

    /**
     * 发布开始/暂停事件
     *
     * @param experimentRestartRequest
     */
    public void restart(ExperimentRestartRequest experimentRestartRequest) {
        ExperimentEvent experimentEvent;
        // 以服务端时间为准
        experimentRestartRequest.setCurrentTime(DateUtil.date());
        if (experimentRestartRequest.getPaused()) {
            experimentEvent = new SuspendEvent(experimentRestartRequest);
        } else {
            experimentEvent = new StartEvent(experimentRestartRequest);
        }
        applicationEventPublisher.publishEvent(experimentEvent);
    }

    /**
     * @param
     * @return
     * @说明: 管理端根据角色获取实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public PageResponse<ExperimentListResponse> pageByRole(PageExperimentRequest pageExperimentRequest) {
        //查询参与者参加的实验列表
        Page page = new Page<ExperimentListResponse>();
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.setSize(pageExperimentRequest.getPageSize());
        // todo 是否是管理员，如果是管理员，则查询所有记录，如果是老师，根据accountId查询自己的分配的实验列表，如果是学生根据accountId查询自己参与的实验
        AccountRoleResponse accountRoleByPrincipalId = accountRoleApi.getAccountRoleByPrincipalId(pageExperimentRequest.getAccountId());
        String roleCode = accountRoleByPrincipalId.getRoleCode();
        if (pageExperimentRequest.getOrder() != null) {
            String[] array = (String[]) pageExperimentRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageExperimentRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }
        if (roleCode.equals("ADMIN")) {
            QueryWrapper<ExperimentInstanceEntity> queryWrapper = new QueryWrapper();
            if (StringUtils.isNotEmpty(pageExperimentRequest.getKeyword())) {
                queryWrapper.like("experiment_name", pageExperimentRequest.getKeyword())
                        .or().like("case_name", pageExperimentRequest.getKeyword())
                        .or().like("appointor_name", pageExperimentRequest.getKeyword());
            }
            queryWrapper.eq("app_Id", "3");
            queryWrapper.orderByDesc("start_time");
            page = experimentInstanceService.page(page, queryWrapper);
        } else {
            if (roleCode.equals("TEACHER")) {
                page = experimentInstanceService.page(page, experimentInstanceService.lambdaQuery()
                        .like(ExperimentInstanceEntity::getExperimentParticipatorIds, accountRoleByPrincipalId.getPrincipalId())
                        .eq(ExperimentInstanceEntity::getAppId, "3")
                        .orderByDesc(ExperimentInstanceEntity::getStartTime)
                        .getWrapper());
            } else {
                page = page.setTotal(0).setCurrent(0).setSize(0).setRecords(new ArrayList<>());
            }
        }
        PageResponse pageInfo = experimentInstanceService.getPageInfo(page, ExperimentListResponse.class);
        // 获取参与教师
        List<ExperimentListResponse> listResponses = pageInfo.getList();
        if (listResponses != null && listResponses.size() > 0) {
            listResponses.forEach(response -> {
                List<ExperimentParticipatorEntity> participatorList = experimentParticipatorService.lambdaQuery()
                        .eq(ExperimentParticipatorEntity::getExperimentInstanceId, response.getExperimentInstanceId())
                        .isNull(ExperimentParticipatorEntity::getExperimentGroupId)
                        .list();
                StringBuilder sb = new StringBuilder();
                if (participatorList != null && participatorList.size() > 0) {
                    participatorList.forEach(participator -> {
                        sb.append(participator.getAccountName()).append(",");
                    });
                }
                if (StringUtils.isNotEmpty(sb)) {
                    String str = sb.substring(0, sb.length() - 1);
                    response.setParticipators(str);
                }
            });
        }
        pageInfo.setList(listResponses);
        return pageInfo;
    }
}