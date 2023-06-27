package org.dows.hep.rest.tenant.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.api.tenant.experiment.request.*;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.api.user.experiment.response.CountDownResponse;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.biz.user.experiment.ExperimentParticipatorBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lait.zhang
 * @description project descr:实验:实验管理
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验管理", description = "实验管理")
public class ExperimentManageRest {
    private final ExperimentManageBiz experimentManageBiz;
    private final ExperimentParticipatorBiz experimentParticipatorBiz;
    private final ExperimentTimerBiz experimentTimerBiz;

    /**
     * 获取实验倒计时
     *
     * @param
     * @return
     */
    @Operation(summary = "获取租户端实验倒计时")
    @GetMapping("v1/tenantExperiment/experimentTimer/countdown")
    public CountDownResponse countdown(@RequestParam String experimentInstanceId) {

        CountDownResponse countdown = experimentTimerBiz.countdown(experimentInstanceId);

        return countdown;
    }
    /**
     * 分配实验
     *
     * @param
     * @return
     */
    @Operation(summary = "分配实验")
    @PostMapping("v1/tenantExperiment/experimentManage/allot")
    public String allot(@RequestBody @Validated CreateExperimentRequest createExperiment, HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账ID
        String accountId = map.get("accountId").toString();
        return experimentManageBiz.allot(createExperiment,accountId);
    }

    /**
     * 案例机构和人物复制到实验
     *
     * @param
     * @return
     */
    @Operation(summary = "案例机构和人物复制到实验")
    @PostMapping("v1/tenantExperiment/experimentManage/copyExperimentPersonAndOrg")
    public Boolean copyExperimentPersonAndOrg(@RequestBody @Validated List<CreateExperimentRequest> createExperimentList) {
        return experimentManageBiz.copyExperimentPersonAndOrg(createExperimentList);
    }


    /**
     * 实验小组保存案例人物
     *
     * @param
     * @return
     */
    @Operation(summary = "实验小组保存案例人物")
    @PostMapping("v1/tenantExperiment/experimentManage/addExperimentGroupPerson")
    public Boolean addExperimentGroupPerson(@RequestBody @Validated CreateExperimentRequest request) {
        return experimentManageBiz.addExperimentGroupPerson(request);
    }

    /**
     * 实验分组
     *
     * @param
     * @return
     */
    @Operation(summary = "实验分组")
    @PostMapping("v1/tenantExperiment/experimentManage/grouping")
    public Boolean grouping(@RequestBody @Validated ExperimentGroupSettingRequest groupSetting) {
        return experimentManageBiz.grouping(groupSetting);
    }

    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验列表")
    @GetMapping("v1/tenantExperiment/experimentManage/list")
    public List<ExperimentListResponse> list(ExperimentQueryRequest experimentQueryRequest) {
        return experimentManageBiz.list(experimentQueryRequest);
    }


    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "分页获取实验列表")
    @GetMapping("v1/tenantExperiment/experimentManage/page")
    public PageResponse<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        return experimentManageBiz.pageByRole(pageExperimentRequest);
    }
    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "根据组名分页获取实验列表")
    @GetMapping("v1/tenantExperiment/experimentManage/pageByGroupName")
    public PageResponse<ExperimentListResponse> pageByGroupName(PageExperimentRequest pageExperimentRequest) {
        return experimentParticipatorBiz.pageByGroupName(pageExperimentRequest);

    }




    /**
     * 获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "开始/暂停实验")
    @PostMapping("v1/tenantExperiment/experimentManage/restart")
    public void restart(@RequestBody @Validated ExperimentRestartRequest experimentRestartRequest) {
        experimentManageBiz.restart(experimentRestartRequest);
    }


}