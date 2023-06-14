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
import org.dows.hep.entity.ExperimentViewMonitorFollowupEntity;
import org.simpleframework.xml.core.Validate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @PostMapping("v1/userExperiment/experimentOrgView/listFollowup")
    public List<ExperimentViewMonitorFollowupEntity> listFollowup(@RequestBody @Validated FindFollowupDefRequest findFollowupDef ) {
        return experimentOrgViewBiz.listFollowup(findFollowupDef);
    }

    /**
    * 监测随访：获取随访表内容
    * @param
    * @return
    */
    @Operation(summary = "监测随访：获取随访表内容")
    @GetMapping("v1/userExperiment/experimentOrgView/getFollowupDef/{experimentViewMonitorFollowupId}/{appId}/{experimentPersonId}/{periods}")
    public List<ExperimentIndicatorResponse> getFollowupDef(@PathVariable @Validated String experimentViewMonitorFollowupId,
                                              @PathVariable @Validated String appId,
                                              @PathVariable @Validated String experimentPersonId,
                                              @PathVariable @Validated String periods
                                              ) {
        return experimentOrgViewBiz.getFollowupDef(experimentViewMonitorFollowupId,appId,experimentPersonId,periods);
    }

    /**
    * 监测随访：保存随访设置，频率，表格
    * @param
    * @return
    */
    @Operation(summary = "监测随访：保存随访设置，频率，表格")
    @PostMapping("v1/userExperiment/experimentOrgView/setFollowup")
    public Boolean setFollowup(@RequestBody @Validated SetFollowupRequest setFollowup,HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账户和名称
        String accountId = map.get("accountId").toString();
        String accountName = map.get("accountName").toString();
        return experimentOrgViewBiz.setFollowup(setFollowup,accountId,accountName);
    }

    /**
     * 监测随访：实验暂停导致时间延后
     * @param
     * @return
     */
    @Operation(summary = "监测随访：实验暂停导致时间延后")
    @PostMapping("v1/userExperiment/experimentOrgView/delayFollowupTimer")
    public Boolean delayFollowupTimer(@RequestParam @Validate long diffTime,
                                      @RequestParam @Validate String appId,
                                      @RequestParam @Validated String experimentInstanceId,
                                      HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账户和名称
        String accountId = map.get("accountId").toString();
        String accountName = map.get("accountName").toString();
        return experimentOrgViewBiz.delayFollowupTimer(diffTime,appId,experimentInstanceId,accountId,accountName);
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
    public Boolean saveFollowup(@RequestBody @Validated SaveFollowupRequest saveFollowup,HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账户和名称
        String accountId = map.get("accountId").toString();
        String accountName = map.get("accountName").toString();
        return experimentOrgViewBiz.saveFollowup(saveFollowup,accountId,accountName);
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
     * 查看指标：查看基本信息查看
     * @param
     * @return
     */
    @Operation(summary = "查看指标：查看基本信息查看")
    @GetMapping("v1/userExperiment/experimentOrgView/getIndicatorBaseInfo/{experimentIndicatorViewBaseInfoId}/{appId}/{experimentPersonId}/{periods}")
    public Map<String,Object> getIndicatorBaseInfo(@PathVariable @Validated String experimentIndicatorViewBaseInfoId,
                                                   @PathVariable @Validated String appId,
                                                   @PathVariable @Validated String experimentPersonId,
                                                   @PathVariable @Validated String periods
                                        ) {
        return experimentOrgViewBiz.getIndicatorBaseInfo(experimentIndicatorViewBaseInfoId,appId,experimentPersonId,periods);
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

//    /**
//     *
//     * ：根据指标分类ID获取所有符合条件的数据
//     * @param
//     * @return
//     */
//    @Operation(summary = "二级类别：根据指标分类ID获取所有符合条件的数据")
//    @GetMapping("v1/userExperiment/experimentOrgJudge/getIndicatorViewPhysicalExamByCategoryId/{indicatoryCategoryId}")
//    public List<ExperimentIndicatorJudgePhysicalExamResponse> getIndicatorViewPhysicalExamByCategoryId(@PathVariable String indicatoryCategoryId) {
//        return experimentOrgViewBiz.getIndicatorViewPhysicalExamByCategoryId(indicatoryCategoryId);
//    }

    /**
     *
     * 二级类别：根据指标分类ID获取所有符合条件的数据
     * @param
     * @return
     */
    @Operation(summary = "二级类别：根据指标分类ID获取所有符合条件的数据")
    @PostMapping("v1/userExperiment/experimentOrgJudge/getIndicatorViewPhysicalExamByCategoryIds")
    public List<ExperimentIndicatorJudgePhysicalExamResponse> getIndicatorViewPhysicalExamByCategoryIds(@RequestBody Set<String> experimentIndicatoryCategoryIds) {
        return experimentOrgViewBiz.getIndicatorViewPhysicalExamByCategoryIds(experimentIndicatoryCategoryIds);
    }

    /**
     *
     * 四级类别：根据指标分类ID获取所有符合条件的数据
     * @param
     * @return
     */
    @Operation(summary = "四级类别：根据指标分类ID获取所有符合条件的数据")
    @PostMapping("v1/userExperiment/experimentOrgJudge/getIndicatorViewSupportExamByCategoryIds")
    public List<ExperimentIndicatorJudgeSupportExamResponse> getIndicatorViewSupportExamByCategoryIds(@RequestBody Set<String> experimentIndicatoryCategoryIds) {
        return experimentOrgViewBiz.getIndicatorViewSupportExamByCategoryIds(experimentIndicatoryCategoryIds);
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