package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.biz.eval.EvalHealthIndexBiz;
import org.dows.hep.biz.eval.EvalPersonIndicatorBiz;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotManager;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.user.experiment.ToolBiz;
import org.springframework.web.bind.annotation.*;

/**
 * @folder user-hep/tools
 * @author : wuzl
 * @date : 2023/8/31 12:15
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "开发用工具", description = "开发用工具")
public class ToolRest {

    private final ToolBiz toolBiz;

    private final EvalPersonIndicatorBiz evalPersonIndicatorBiz;

    private final EvalHealthIndexBiz evalHealthIndexBiz;

    private final SnapshotManager snapshotManager;





    @Operation(summary = "获取webSocket连接状态")
    @GetMapping("v1/tool/getWebSocketState")
    public String getWebSocketState(@RequestParam String exptId){
        return toolBiz.getWebSocketState(exptId);
    }


    @GetMapping("v1/tool/ping")
    public String ping(){
        return toolBiz.ping();
    }

    @Operation(summary = "指标计算")
    @PostMapping("v1/tool/evalPersonIndicator")
    public void evalPersonIndicator(@RequestBody RsCalculatePersonRequestRs req)  {
        evalPersonIndicatorBiz.evalPersonIndicator(req);
    }

    @Operation(summary = "健康指数计算")
    @PostMapping("v1/tool/evalHealthIndex")
    public void evalHealthIndex(@RequestBody ExperimentRsCalculateAndCreateReportHealthScoreRequestRs req)  {
        evalHealthIndexBiz.evalPersonHealthIndex(req);
    }

    @Operation(summary = "功能点结算")
    @PostMapping("v1/tool/evalOrgFunc")

    public void evalOrgFunc(@RequestBody RsExperimentCalculateFuncRequest req) {
        toolBiz.evalOrgFunc(req);
    }

    @Operation(summary = "期末翻转")
    @PostMapping("v1/tool/evalPeriodEnd")
    public void evalPeriodEnd(@RequestBody RsCalculatePeriodsRequest req)  {
        toolBiz.evalPeriodEnd(req);
    }

    @Operation(summary = "计算案例人物HP")
    @PostMapping("v1/tool/evalCasePersonHP")
    public void evalCasePersonHP(@RequestBody CaseRsCalculateHealthScoreRequestRs req){
        toolBiz.evalCasePersonHP(req);
    }

    @Operation(summary = "期末翻转-算分")
    @PostMapping("v1/tool/evalPeriodEndScore")
    public void evalPeriodEndScore(@RequestBody RsCalculatePeriodsRequest req)  {
        toolBiz.evalPeriodEndScore(req);
    }

    @Operation(summary = "期末翻转-判断类算分")
    @PostMapping("v1/tool/evalPeriodJudgeScore")
    public void evalPeriodJudgeScore(@RequestBody RsCalculatePeriodsRequest req){
        toolBiz.evalPeriodJudgeScore(req);
    }

    @Operation(summary = "期末翻转-医疗占比得分")
    @PostMapping("v1/tool/evalPeriodMoneyScore")

    public void evalPeriodMoneyScore(RsCalculatePeriodsRequest req){
        toolBiz.evalPeriodMoneyScore(req);
    }

    @Operation(summary = "条件事件触发")
    @PostMapping("v1/tool/raiseEvent")
    public void raiseEvent(@RequestBody RsCalculatePersonRequestRs req)  {
        toolBiz.raiseEvent(req);
    }

    @Operation(summary = "实验数据复制")
    @PostMapping(value = "v1/tool/snapshot",produces = {})
    public void snapshot(@RequestBody SnapshotRequest req)  {
        snapshotManager.write(req, EnumSnapshotType.CASEIndicatorExpressionRef,
                EnumSnapshotType.CASEIndicatorExpression,
                EnumSnapshotType.CASEIndicatorExpressionItem);
    }


}
