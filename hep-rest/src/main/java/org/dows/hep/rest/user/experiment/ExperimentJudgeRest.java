package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.JudgeGoalResponse;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.user.experiment.request.FindInterveneList4ExptRequest;
import org.dows.hep.api.user.experiment.request.SaveExptJudgeGoalRequest;
import org.dows.hep.api.user.experiment.response.ExptJudgeGoalResponse;
import org.dows.hep.api.user.experiment.response.SaveExptOperateResponse;
import org.dows.hep.biz.user.experiment.ExperimentJudgeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  @description project descr:判断指标
 *  @folder user-hep/判断指标/管理目标
 *
 * @author : wuzl
 * @date : 2023/10/19 11:27
 */

@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标", description = "判断指标")

public class ExperimentJudgeRest {

    private final ExperimentJudgeBiz experimentJudgeBiz;

    @Operation(summary = "学生端获取管理目标列表")
    @PostMapping("v1/userExperiment/experimentJudge/listJudgeGoal")
    public List<JudgeGoalResponse> listJudgeGoal(@RequestBody @Validated FindInterveneList4ExptRequest req){
        return experimentJudgeBiz.listJudgeGoal(req);
    }

    @Operation(summary = "学生端获取管理目标保存记录")
    @PostMapping("v1/userExperiment/experimentJudge/getJudgeGoal")
    public ExptJudgeGoalResponse getJudgeGoal(@RequestBody @Validated ExptOperateOrgFuncRequest req){
        return experimentJudgeBiz.getJudgeGoal(req);
    }

    @Operation(summary = "学生端保存管理目标")
    @PostMapping("v1/userExperiment/experimentJudge/saveJudgeGoal")
    public SaveExptOperateResponse saveJudgeGoal(@RequestBody @Validated SaveExptJudgeGoalRequest req, HttpServletRequest httpReq) {
        return experimentJudgeBiz.saveJudgeGoal(req, httpReq);
    }

}
