package org.dows.hep.rest.tenant.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.core.CreateExperimentForm;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.api.tenant.experiment.request.*;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.api.tenant.experiment.response.ExptSchemeGroupReviewResponse;
import org.dows.hep.api.tenant.experiment.vo.ExptSchemeScoreReviewVO;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentSchemeScoreBiz;
import org.dows.hep.biz.user.experiment.ExperimentBaseBiz;
import org.dows.hep.biz.user.experiment.ExperimentParticipatorBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lait.zhang
 * @folder tenant-hep/实验管理
 * @description project descr:实验:实验管理
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验管理", description = "实验管理")
public class ExperimentManageRest {
    private final ExperimentBaseBiz baseBiz;
    private final ExperimentManageBiz experimentManageBiz;
    private final ExperimentParticipatorBiz experimentParticipatorBiz;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentSchemeScoreBiz experimentSchemeScoreBiz;

    /**
     * 获取实验倒计时
     *
     * @param
     * @return
     */
    @Operation(summary = "获取租户端实验倒计时")
    @GetMapping("v1/tenantExperiment/experimentTimer/countdown")
    public IntervalResponse countdown(@RequestParam String appId,@RequestParam String experimentInstanceId) {

        IntervalResponse countdown = experimentTimerBiz.countdown(appId, experimentInstanceId);

        return countdown;
    }

    /**
     * 分配实验
     *
     * @param
     * @return
     */
    @Resubmit(duration = 5)
    @Operation(summary = "分配实验")
    @PostMapping("v1/tenantExperiment/experimentManage/allot")
    public String allot(@RequestBody @Validated CreateExperimentRequest createExperiment, HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账ID
        String accountId = map.get("accountId").toString();
        //return "";
        return experimentManageBiz.allot(createExperiment, accountId);
    }

    /**
     * 获取实验分配信息
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验分配信息")
    @GetMapping("v1/tenantExperiment/experimentManage/getAllotData")
    public CreateExperimentForm getAllotData(String experimentInstanceId) {
        return experimentManageBiz.getAllotData(experimentInstanceId, "");
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
     * 分页获取实验列表
     *
     * @param
     * @return
     */
    @Operation(summary = "分页获取实验列表")
    @GetMapping("v1/tenantExperiment/experimentManage/page")
    public PageResponse<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        return experimentManageBiz.pageByRole(pageExperimentRequest);
    }

    @Operation(summary = "删除实验")
    @DeleteMapping("v1/tenantExperiment/experimentManage/delete")
    public Boolean delete(DeleteExperimentRequest deleteExperimentRequest) {
        return experimentManageBiz.delete(deleteExperimentRequest);
    }

    /**
     * 根据组名分页获取实验列表
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
     * 开始/暂停实验
     *
     * @param
     * @return
     */
    @Operation(summary = "开始/暂停实验")
    @PostMapping("v1/tenantExperiment/experimentManage/restart")
    public void restart(@RequestBody @Validated ExperimentRestartRequest experimentRestartRequest) {
        experimentManageBiz.restart(experimentRestartRequest);
    }

    /**
     * 获取方案设计评分小组列表
     *
     * @param
     * @return
     */
    @Operation(summary = "获取方案设计评分小组列表")
    @GetMapping("v1/tenantExperiment/experimentManage/listSchemeGroupReview")
    public List<ExptSchemeGroupReviewResponse> listSchemeGroupReview(@RequestParam("exptInstanceId") String exptInstanceId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeScoreBiz.listSchemeGroupReview(exptInstanceId, accountId);
    }

    /**
     * 是否可以评分
     *
     * @param
     * @return
     */
    @Operation(summary = "是否可以评分")
    @GetMapping("v1/tenantExperiment/experimentManage/canReview")
    public boolean canReview(@RequestParam("exptInstanceId") String exptInstanceId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeScoreBiz.canReview(exptInstanceId, accountId);
    }

    /**
     * 获取方案设计评分详情
     *
     * @param
     * @return
     */
    @Operation(summary = "获取方案设计评分详情")
    @GetMapping("v1/tenantExperiment/experimentManage/getSchemeReviewDetail")
    public ExptSchemeScoreReviewVO getSchemeReviewDetail(@RequestParam("exptInstanceId") String exptInstanceId, @RequestParam("exptGroupId") String exptGroupId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeScoreBiz.getSchemeScoreReview(exptInstanceId, accountId, exptGroupId);
    }

    /**
     * 提交方案设计评分详情
     *
     * @param
     * @return
     */
    @Operation(summary = "提交方案设计评分详情")
    @PostMapping("v1/tenantExperiment/experimentManage/submitSchemeScore")
    public String submitSchemeScore(@RequestBody @Validated ExperimentSchemeScoreRequest schemeScoreRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentSchemeScoreBiz.submitSchemeScore(schemeScoreRequest, accountId);
    }

    /**
     * 修改得分
     */
//    @Operation(summary = "修改得分")
//    @GetMapping("v1/tenantExperiment/experimentManage/reUpdSchemeScore")
//    public void reUpdSchemeScore(@RequestParam("exptInstanceId") String exptInstanceId) {
//        experimentSchemeScoreBiz.reUpdSchemeScore(exptInstanceId, 100);
//    }
}