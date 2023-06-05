package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgViewBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
* @description project descr:实验:机构操作-查看指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构操作-查看指标", description = "机构操作-查看指标")
public class ExperimentOrgViewRest {
    private final ExperimentOrgViewBiz experimentOrgViewBiz;

    /**
    * 获取人物基本信息（健管，体检，医院）
    * @param
    * @return
    */
    @Operation(summary = "获取人物基本信息（健管，体检，医院）")
    @PostMapping("v1/userExperiment/experimentOrgView/getOrgPersonBasic")
    public OrgPersonBasicResponse getOrgPersonBasic(@RequestBody @Validated FindOrgPersonBasicRequest findOrgPersonBasic ) {
        return experimentOrgViewBiz.getOrgPersonBasic(findOrgPersonBasic);
    }

    /**
    * 监测随访：获取类别，随访表列表
    * @param
    * @return
    */
    @Operation(summary = "监测随访：获取类别，随访表列表")
    @PostMapping("v1/userExperiment/experimentOrgView/listFollowupDef")
    public List<FollowupDefResponse> listFollowupDef(@RequestBody @Validated FindFollowupDefRequest findFollowupDef ) {
        return experimentOrgViewBiz.listFollowupDef(findFollowupDef);
    }

    /**
    * 监测随访：获取随访表内容
    * @param
    * @return
    */
    @Operation(summary = "监测随访：获取随访表内容")
    @GetMapping("v1/userExperiment/experimentOrgView/getFollowupDef")
    public FollowupDefResponse getFollowupDef(@Validated String indicatorViewMonitorFollowupId) {
        return experimentOrgViewBiz.getFollowupDef(indicatorViewMonitorFollowupId);
    }

    /**
    * 监测随访：保存随访设置，频率，表格
    * @param
    * @return
    */
    @Operation(summary = "监测随访：保存随访设置，频率，表格")
    @PostMapping("v1/userExperiment/experimentOrgView/setFollowup")
    public Boolean setFollowup(@RequestBody @Validated SetFollowupRequest setFollowup ) {
        return experimentOrgViewBiz.setFollowup(setFollowup);
    }

    /**
    * 监测随访：获取随访状态，是否可随访
    * @param
    * @return
    */
    @Operation(summary = "监测随访：获取随访状态，是否可随访")
    @PostMapping("v1/userExperiment/experimentOrgView/getFollowupState")
    public FollowupStateResponse getFollowupState(@RequestBody @Validated FindFollowupStateRequest findFollowupState ) {
        return experimentOrgViewBiz.getFollowupState(findFollowupState);
    }

    /**
    * 监测随访：开始随访（保存随访记录）
    * @param
    * @return
    */
    @Operation(summary = "监测随访：开始随访（保存随访记录）")
    @PostMapping("v1/userExperiment/experimentOrgView/saveFollowup")
    public SaveFollowupResponse saveFollowup(@RequestBody @Validated SaveFollowupRequest saveFollowup ) {
        return experimentOrgViewBiz.saveFollowup(saveFollowup);
    }

    /**
    * 监测随访：查看随访报告
    * @param
    * @return
    */
    @Operation(summary = "监测随访：查看随访报告")
    @GetMapping("v1/userExperiment/experimentOrgView/getFollowUpReport")
    public FollowupReportInfoResponse getFollowUpReport(@Validated String operateOrgFuncId) {
        return experimentOrgViewBiz.getFollowUpReport(operateOrgFuncId);
    }

    /**
    * 体格检查+辅助检查：获取检查类别+项目
    * @param
    * @return
    */
    @Operation(summary = "体格检查+辅助检查：获取检查类别+项目")
    @PostMapping("v1/userExperiment/experimentOrgView/listOrgViewCategs")
    public List<CategsWithStateResponse> listOrgViewCategs(@RequestBody @Validated FindOrgViewCategsRequest findOrgViewCategs ) {
        return experimentOrgViewBiz.listOrgViewCategs(findOrgViewCategs);
    }

    /**
    * 体格检查+辅助检查：执行检查(开检查单)
    * @param
    * @return
    */
    @Operation(summary = "体格检查+辅助检查：执行检查(开检查单)")
    @PostMapping("v1/userExperiment/experimentOrgView/saveOrgView")
    public SaveOrgViewResponse saveOrgView(@RequestBody @Validated SaveOrgViewRequest saveOrgView ) {
        return experimentOrgViewBiz.saveOrgView(saveOrgView);
    }

    /**
    * 体格检查+辅助检查：获取最新检查报告
    * @param
    * @return
    */
    @Operation(summary = "体格检查+辅助检查：获取最新检查报告")
    @PostMapping("v1/userExperiment/experimentOrgView/getOrgViewReport")
    public List<OperateOrgFuncSnapRequest> getOrgViewReport(@RequestBody @Validated GetOrgViewReportRequest orgViewReport) {
        return experimentOrgViewBiz.getOrgViewReport(orgViewReport);
    }

    /**
     * 基本信息：查看
     * @param
     * @return
     */
    @Operation(summary = "基本信息：查看")
    @GetMapping("v1/userExperiment/experimentOrgView/getIndicatorBaseInfo/{indicatorViewBaseInfoId}/{appId}")
    public Boolean getIndicatorBaseInfo(@PathVariable @Validated String indicatorViewBaseInfoId,@PathVariable @Validated String appId) {
        return experimentOrgViewBiz.getIndicatorBaseInfo(indicatorViewBaseInfoId,appId);
    }

    /**
     * 体格检查+辅助检查：体格检查+辅助检查保存
     * @param
     * @return
     */
    @Operation(summary = "体格检查+辅助检查：体格检查+辅助检查保存")
    @PostMapping("v1/userExperiment/experimentOrgView/savePhysiqueAndAuxiliary")
    public Boolean savePhysiqueAndAuxiliary(@RequestBody @Validated List<GetOrgViewReportRequest> reportRequestList, HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账户和名称
        String accountId = map.get("accountId").toString();
        String accountName = map.get("accountName").toString();
        return experimentOrgViewBiz.savePhysiqueAndAuxiliary(reportRequestList,accountId,accountName);
    }

    /**
     *
     * 二级类别：根据指标分类ID获取所有符合条件的数据
     * @param
     * @return
     */
    @Operation(summary = "二级类别：根据指标分类ID获取所有符合条件的数据")
    @GetMapping("v1/userExperiment/experimentOrgJudge/getIndicatorViewPhysicalExamByCategoryId/{indicatoryCategoryId}")
    public List<ExperimentIndicatorJudgePhysicalExamResponse> getIndicatorViewPhysicalExamByCategoryId(@PathVariable String indicatoryCategoryId) {
        return experimentOrgViewBiz.getIndicatorViewPhysicalExamByCategoryId(indicatoryCategoryId);
    }

    /**
     * 体格检查：获取判断结果
     * @param
     * @return
     */
    @Operation(summary = "体格检查：获取判断结果")
    @PostMapping("v1/userExperiment/experimentOrgView/getIndicatorPhysicalExamVerifiResults")
    public List<GetOrgViewReportResponse> getIndicatorPhysicalExamVerifiResults(@RequestBody @Validated List<GetOrgViewReportRequest> reportRequestList) {
        return experimentOrgViewBiz.getIndicatorPhysicalExamVerifiResults(reportRequestList);
    }

    /**
     * 辅助检查：获取判断结果
     * @param
     * @return
     */
    @Operation(summary = "辅助检查：获取判断结果")
    @PostMapping("v1/userExperiment/experimentOrgView/getIndicatorSupportExamVerifiResults")
    public List<GetOrgViewReportResponse> getIndicatorSupportExamVerifiResults(@RequestBody @Validated List<GetOrgViewReportRequest> reportRequestList) {
        return experimentOrgViewBiz.getIndicatorSupportExamVerifiResults(reportRequestList);
    }
}