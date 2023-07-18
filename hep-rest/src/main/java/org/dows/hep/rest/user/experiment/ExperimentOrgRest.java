package org.dows.hep.rest.user.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.core.BaseExptRequest;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:实验:机构操作
* @folder user-hep/机构操作
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构操作", description = "机构操作")
public class ExperimentOrgRest {
    private final ExperimentOrgBiz experimentOrgBiz;

    /**
    * 获取机构人物列表，挂号费用，挂号状态
    * @param
    * @return
    */
    @Operation(summary = "获取机构人物列表，挂号费用，挂号状态")
    @PostMapping("v1/userExperiment/experimentOrg/pageOrgPersons")
    public OrgPersonResponse pageOrgPersons(@RequestBody @Validated FindOrgPersonsRequest findOrgPersons ) {
        return experimentOrgBiz.pageOrgPersons(findOrgPersons);
    }

    /**
     * 获取实验人物列表
     * @param
     * @return
     */
    @Operation(summary = "获取实验人物列表")
    @PostMapping("v1/userExperiment/experimentOrg/pageExperimentPersons")
    public Page<ExperimentPersonResponse> pageExperimentPersons(@RequestBody @Validated ExperimentPersonRequest personRequest) {
        return experimentOrgBiz.pageExperimentPersons(personRequest);
    }

    /**
    * 挂号：医院，体检中心
    * @param
    * @return
    */
    @Operation(summary = "挂号：医院，体检中心")
    @PostMapping("v1/userExperiment/experimentOrg/startOrgFlow")
    public Boolean startOrgFlow(@RequestBody @Validated StartOrgFlowRequest startOrgFlow, HttpServletRequest request ) {
        return experimentOrgBiz.startOrgFlow(startOrgFlow, request);
    }

    /**
    * 获取机构通知列表
    * @param
    * @return
    */
    @Operation(summary = "获取机构通知列表")
    @PostMapping("v1/userExperiment/experimentOrg/pageOrgNotice")
    public Page<OrgNoticeResponse> pageOrgNotice(@RequestBody @Validated BaseExptRequest findOrgNotice ) {
        return experimentOrgBiz.pageOrgNotice(findOrgNotice);
    }

    @Operation(summary = "获取机构通知详情（主要是事件操作提示+处理措施列表）")
    @PostMapping("v1/userExperiment/experimentOrg/getOrgNotice")
    public OrgNoticeResponse getOrgNotice(@RequestBody @Validated FindOrgNoticeRequest findOrgNotice) throws JsonProcessingException{
        return experimentOrgBiz.getOrgNotice(findOrgNotice);
    }
    @Operation(summary = "处理突发事件")
    @PostMapping("v1/userExperiment/experimentOrg/saveOrgNoticeAction")
    public OrgNoticeResponse saveOrgNoticeAction(@RequestBody @Validated SaveNoticeActionRequest saveNoticeAction, HttpServletRequest request) throws JsonProcessingException{
        return experimentOrgBiz.saveOrgNoticeAction(saveNoticeAction,request);
    }

    /**
    * 获取机构报告列表
    * @param
    * @return
    */
    @Operation(summary = "获取机构报告列表")
    @PostMapping("v1/userExperiment/experimentOrg/pageOrgReport")
    public Page<OrgFlowReportResponse> pageOrgReport(@RequestBody @Validated FindOrgReportRequest findOrgReport)  {
        return experimentOrgBiz.pageOrgReport(findOrgReport);
    }

    /**
     * 获取机构报告详情
     * @param orgReportRequest
     * @return
     */
    @Operation(summary = "获取机构报告详情")
    @PostMapping("v1/userExperiment/experimentOrg/getOrgReportInfo")
    public ExptOrgFlowReportResponse getOrgReportInfo(@RequestBody @Validated ExptOrgFlowReportRequest orgReportRequest){
        return experimentOrgBiz.getOrgReportInfo(orgReportRequest);
    }

    /**
    * 查看体检报告详情
    * @param
    * @return
    */
    @Operation(summary = "查看体检报告详情")
    @GetMapping("v1/userExperiment/experimentOrg/getPhysicalExamReport")
    public PhysicalExamReportInfoResponse getPhysicalExamReport(@Validated String operateFlowId) {
        return experimentOrgBiz.getPhysicalExamReport(operateFlowId);
    }

    /**
    * 查看诊疗报告详情
    * @param
    * @return
    */
    @Operation(summary = "查看诊疗报告详情")
    @GetMapping("v1/userExperiment/experimentOrg/getTreatReport")
    public TreatReportInfoResponse getTreatReport(@Validated String operateFlowId) {
        return experimentOrgBiz.getTreatReport(operateFlowId);
    }


}