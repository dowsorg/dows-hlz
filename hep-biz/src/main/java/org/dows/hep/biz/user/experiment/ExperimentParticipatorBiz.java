package org.dows.hep.biz.user.experiment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountRoleApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountRoleResponse;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentStatusCode;
import org.dows.hep.api.enums.EnumParticipatorType;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.api.user.experiment.ExptSettingModeEnum;
import org.dows.hep.api.user.experiment.request.GetExperimentGroupCaptainRequest;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.api.user.experiment.response.GetExperimentGroupCaptainResponse;
import org.dows.hep.biz.util.EntityUtil;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentParticipatorBiz {
    private final ExperimentSettingBiz experimentSettingBiz;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验小组
    private final ExperimentGroupService experimentGroupService;
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;

    private final AccountRoleApi accountRoleApi;

    private final AccountInstanceApi accountInstanceApi;

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
        Page page = new Page<ExperimentParticipatorEntity>();
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
            QueryWrapper<ExperimentParticipatorEntity> queryWrapper = new QueryWrapper();
            queryWrapper.select(EntityUtil.distinctColumn(ExperimentParticipatorEntity.class, "experimentInstanceId"));
            queryWrapper.likeLeft("experiment_name", pageExperimentRequest.getKeyword());
            page = experimentParticipatorService.page(page, queryWrapper);

        } else {
            if (!StrUtil.isBlank(pageExperimentRequest.getAccountId())) {

                page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                        .eq(ExperimentParticipatorEntity::getAccountId, pageExperimentRequest.getAccountId())
                        .orderByDesc(ExperimentParticipatorEntity::getExperimentStartTime)
                        .getWrapper());
            } else {
                page = page.setTotal(0).setCurrent(0).setSize(0).setRecords(new ArrayList<>());
            }
        }
        PageResponse pageInfo = experimentParticipatorService.getPageInfo(page, ExperimentListResponse.class);
        return pageInfo;
    }

    /**
     * @param
     * @return
     * @说明: 学生端分页实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public PageResponse<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        //查询参与者参加的实验列表
        Page<ExperimentParticipatorEntity> page = new Page<>();
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.setSize(pageExperimentRequest.getPageSize());

        if (pageExperimentRequest.getOrder() != null) {
            String[] array = (String[]) pageExperimentRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageExperimentRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }

        if (!StrUtil.isBlank(pageExperimentRequest.getAccountId()) && !StrUtil.isBlank(pageExperimentRequest.getExperimentInstanceId())) {// 查询当前用户进行的实验
            page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getAccountId, pageExperimentRequest.getAccountId())
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, pageExperimentRequest.getExperimentInstanceId())
                    .orderByDesc(ExperimentParticipatorEntity::getExperimentStartTime)
                    .getWrapper());
        } else if (!StrUtil.isBlank(pageExperimentRequest.getAccountId())) { // 分页查询用户所参与的实验
            page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getAccountId, pageExperimentRequest.getAccountId())
                    .orderByDesc(ExperimentParticipatorEntity::getExperimentStartTime)
                    .getWrapper());
        } else if (!StrUtil.isBlank(pageExperimentRequest.getExperimentInstanceId())) { // 查询该实验的参与者
            page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, pageExperimentRequest.getExperimentInstanceId())
                    .orderByDesc(ExperimentParticipatorEntity::getExperimentStartTime)
                    .getWrapper());
        } else {//
            page = page.setTotal(0).setCurrent(0).setSize(0).setRecords(new ArrayList<>());
        }

        // 对方案设计模式实验状态二次处理
        if (StrUtil.isNotBlank(pageExperimentRequest.getAccountId())) {
            handleSchemeExptState(page, pageExperimentRequest.getAccountId());
        }

        //为空时list设置为空数组
        PageResponse pageInfo = experimentParticipatorService.getPageInfo(page, ExperimentListResponse.class);
        if (pageInfo.getList() == null || pageInfo.getList().size() == 0) {
            pageInfo.setList(new ArrayList());
        }
        return pageInfo;
    }

    private void handleSchemeExptState(Page<ExperimentParticipatorEntity> page, String accountId) {
        // 获取实验列表
        List<ExperimentParticipatorEntity> records = page.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            // 需要处理的实验
            List<String> exptInstanceIds = records.stream()
                    .map(ExperimentParticipatorEntity::getExperimentInstanceId)
                    .toList();

            // 实验状态变更Map
            Map<String, EnumExperimentState> groupStateMapExptState = groupStateMapExptState(exptInstanceIds, accountId);

            // 变更
            records.forEach(record -> {
                String exptInstanceId = record.getExperimentInstanceId();
                EnumExperimentState exptState = groupStateMapExptState.get(exptInstanceId);
                if (exptState != null) {
                    record.setState(exptState.getState());
                }
            });
        }
    }

    /**
     * @param exptInstanceIds - 实验实例ID集合
     * @param accountId       - 账号ID
     * @return java.util.Map<java.lang.String, org.dows.hep.api.enums.EnumExperimentState>
     * @author fhb
     * @description 将`方案设计模式的`实验状态根据实验小组状态进行调整
     * @date 2023/7/27 11:52
     */
    private Map<String, EnumExperimentState> groupStateMapExptState(List<String> exptInstanceIds, String accountId) {
        Map<String, EnumExperimentState> result = new HashMap<>();
        // 找出实验模式是方案设计的 - exptIds
        List<String> schemeExptIdList = new ArrayList<>();
        Map<String, ExptSettingModeEnum> exptModeCollect = experimentSettingBiz.listExptSettingMode(exptInstanceIds);
        exptModeCollect.forEach((k, v) -> {
            if (ExptSettingModeEnum.SCHEME.equals(v)) {
                schemeExptIdList.add(k);
            }
        });
        if (CollUtil.isEmpty(schemeExptIdList)) {
            return result;
        }

        // 获取实验 && 账号的小组信息 - exptId map groupId
        List<ExperimentParticipatorEntity> partList = experimentParticipatorService.lambdaQuery()
                .in(ExperimentParticipatorEntity::getExperimentInstanceId, schemeExptIdList)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .list();
        List<String> groupIdList = partList.stream()
                .map(ExperimentParticipatorEntity::getExperimentGroupId)
                .toList();
        Map<String, String> exptIdMapGroupId = partList.stream()
                .collect(Collectors.toMap(ExperimentParticipatorEntity::getExperimentInstanceId, ExperimentParticipatorEntity::getExperimentGroupId));

        // 获取实验小组状态 - groupId map groupState
        List<ExperimentGroupEntity> groupList = experimentGroupService.lambdaQuery()
                .in(ExperimentGroupEntity::getExperimentGroupId, groupIdList)
                .list();
        Map<String, Integer> groupIdMapState = groupList.stream()
                .collect(Collectors.toMap(ExperimentGroupEntity::getExperimentGroupId, ExperimentGroupEntity::getGroupState));

        // exptId map groupState
        schemeExptIdList.forEach(exptId -> {
            String groupId = exptIdMapGroupId.get(exptId);
            Integer groupState = groupIdMapState.get(groupId);
            if (EnumExperimentGroupStatus.SCHEMA.getCode().equals(groupState)) {
                result.put(exptId, EnumExperimentState.ONGOING);
            }
            if (EnumExperimentGroupStatus.WAIT_SCHEMA.getCode().equals(groupState)) {
                result.put(exptId, EnumExperimentState.FINISH);
            }
        });

        return result;
    }

    /**
     * @param
     * @return
     * @说明: 教师端获取团队信息（实验参与者分组）
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public PageResponse<ExperimentListResponse> pageByGroupName(PageExperimentRequest pageExperimentRequest) {
        //查询参与者参加的实验列表
        Page page = new Page<ExperimentListResponse>();
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.setSize(pageExperimentRequest.getPageSize());

        page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                .select(ExperimentParticipatorEntity::getExperimentGroupId)
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, pageExperimentRequest.getExperimentInstanceId())
                .isNotNull(ExperimentParticipatorEntity::getExperimentGroupId)
                .groupBy(ExperimentParticipatorEntity::getExperimentGroupId)
                .getWrapper());
        PageResponse pageInfo = experimentParticipatorService.getPageInfo(page, ExperimentListResponse.class);
        // 获取小组参与者
        List<ExperimentListResponse> list = pageInfo.getList();
        if (list != null && list.size() > 0) {
            list.forEach(response -> {
                //获取小组组名和状态
                ExperimentGroupEntity groupEntity = experimentGroupService.lambdaQuery()
                        .eq(ExperimentGroupEntity::getExperimentGroupId, response.getExperimentGroupId())
                        .eq(ExperimentGroupEntity::getExperimentInstanceId, pageExperimentRequest.getExperimentInstanceId())
                        .eq(ExperimentGroupEntity::getDeleted, false)
                        .one();
                //获取实验状态
                ExperimentInstanceEntity instanceEntity = experimentInstanceService.lambdaQuery()
                        .eq(ExperimentInstanceEntity::getExperimentInstanceId, pageExperimentRequest.getExperimentInstanceId())
                        .eq(ExperimentInstanceEntity::getDeleted, false)
                        .one();
                response.setGroupNo(groupEntity.getGroupNo());
                response.setGroupAlias(groupEntity.getGroupAlias());
                response.setGroupName(groupEntity.getGroupName());
                response.setState(instanceEntity.getState());
                response.setStateDescr(response.getStateDescr());
                response.setGroupState(groupEntity.getGroupState());
                response.setGroupStateStr(EnumExperimentGroupStatus.of(groupEntity.getGroupState()).getCastDescr());
                List<ExperimentParticipatorEntity> participatorEntityList = experimentParticipatorService.lambdaQuery()
                        .eq(ExperimentParticipatorEntity::getExperimentGroupId, response.getExperimentGroupId())
                        .list();
                List<ExperimentParticipatorResponse> participatorResponseList = new ArrayList<>();
                // 获取参与者账号及头像
                if (participatorEntityList != null && participatorEntityList.size() > 0) {
                    participatorEntityList.stream()
                            .sorted(Comparator.comparing(ExperimentParticipatorEntity::getParticipatorType))
                            .forEach(participatorEntity -> {
                                AccountInstanceResponse instanceResponse = accountInstanceApi.getPersonalInformationByAccountId(participatorEntity.getAccountId(), "3");
                                ExperimentParticipatorResponse participatorResponse = ExperimentParticipatorResponse
                                        .builder()
                                        .accountName(participatorEntity.getAccountName())
                                        .avatar(instanceResponse != null ? instanceResponse.getAvatar() : "")
                                        .build();
                                participatorResponseList.add(participatorResponse);
                            });
//                    participatorEntityList.forEach(participatorEntity -> {
//                        AccountInstanceResponse instanceResponse = accountInstanceApi.getPersonalInformationByAccountId(participatorEntity.getAccountId(), "3");
//                        ExperimentParticipatorResponse participatorResponse = ExperimentParticipatorResponse
//                                .builder()
//                                .accountName(participatorEntity.getAccountName())
//                                .avatar(instanceResponse != null ? instanceResponse.getAvatar() : "")
//                                .build();
//                        participatorResponseList.add(participatorResponse);
//                    });
                }
                response.setParticipatorList(participatorResponseList);
            });
        }
        pageInfo.setList(list);
        return pageInfo;
    }


    /**
     * 获取实验小组长
     *
     * @param getExperimentGroupCaptainRequest
     * @return
     */
    public GetExperimentGroupCaptainResponse getExperimentGroupRole(GetExperimentGroupCaptainRequest getExperimentGroupCaptainRequest) {

        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, getExperimentGroupCaptainRequest.getExperimentInstanceId())
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, getExperimentGroupCaptainRequest.getExperimentGroupId())
                .eq(ExperimentParticipatorEntity::getAccountId, getExperimentGroupCaptainRequest.getAccountId())
//                .eq(ExperimentParticipatorEntity::getParticipatorType, getExperimentGroupCaptainRequest.getParticipatorType().getCode())
                .oneOpt().orElse(null);
        if (experimentParticipatorEntity == null) {
            throw new ExperimentException(EnumExperimentStatusCode.NOT_CAPTAIN);
//            return null;
        }
        return BeanConvert.beanConvert(experimentParticipatorEntity, GetExperimentGroupCaptainResponse.class);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 是否是组长
     * @date 2023/6/13 10:48
     */
    public Boolean isCaptain(String experimentInstanceId, String experimentGroupId, String accountId) {
        Long count = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .eq(ExperimentParticipatorEntity::getParticipatorType, EnumParticipatorType.CAPTAIN.getCode())
                .count();
        if (count == null || count == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    /**
     * 进入实验
     */
//    enterExperiment
}
