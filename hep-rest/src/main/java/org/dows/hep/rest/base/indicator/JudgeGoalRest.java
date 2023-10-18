package org.dows.hep.rest.base.indicator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.JudgeGoalInfoResponse;
import org.dows.hep.api.base.indicator.response.JudgeGoalResponse;
import org.dows.hep.api.base.intervene.request.DelRefIndicatorRequest;
import org.dows.hep.biz.base.indicator.JudgeGoalBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @description project descr:判断指标:管理目标
 *
 *  @folder admin-hep/判断指标/管理目标
 * @author : wuzl
 * @date : 2023/10/18 16:36
 */

@RequiredArgsConstructor
@RestController
@Tag(name = "管理目标", description = "管理目标")
public class JudgeGoalRest {

    private final JudgeGoalBiz judgeGoalBiz;
    /**
     * 获取管理目标列表
     * @param
     * @return
     */
    @Operation(summary = "获取管理目标列表")
    @PostMapping("v1/baseIndicator/judgeGoal/pageJudgeGoal")
    public Page<JudgeGoalResponse> pageJudgeGoal(@RequestBody @Validated FindJudgeGoalRequest findJudgeGoal ) {
        return judgeGoalBiz.pageJudgeGoal(findJudgeGoal);
    }

    /**
     * 获取管理目标详细信息
     * @param
     * @return
     */
    @Operation(summary = "获取管理目标详细信息")
    @GetMapping("v1/baseIndicator/JudgeGoal/getJudgeGoal")
    public JudgeGoalInfoResponse getJudgeGoal(@Validated String appId,@Validated String indicatorJudgeGoalId) {
        return judgeGoalBiz.getJudgeGoal(new GetJudgeGoalRequest().setAppId(appId).setIndicatorJudgeGoalId(indicatorJudgeGoalId));
    }

    /**
     * 保存管理目标
     * @param
     * @return
     */
    @Operation(summary = "保存管理目标")
    @PostMapping("v1/baseIndicator/JudgeGoal/saveJudgeGoal")
    public Boolean saveJudgeGoal(@RequestBody @Validated SaveJudgeGoalRequest saveJudgeGoal ) {
        return judgeGoalBiz.saveJudgeGoal(saveJudgeGoal);
    }

    /**
     * 启用禁用管理目标
     *
     * @param
     * @return
     */
    @Operation(summary = "启用禁用管理目标")
    @PostMapping("v1/baseIndicator/JudgeGoal/setJudgeGoalState")
    public Boolean setJudgeGoalState(@RequestBody @Validated SetJudgeGoalStateRequest setJudgeGoalStateRequest ) {
        return judgeGoalBiz.setJudgeGoalState(setJudgeGoalStateRequest);
    }

    /**
     * 删除管理目标
     * @param
     * @return
     */
    @Operation(summary = "删除管理目标")
    @DeleteMapping("v1/baseIndicator/JudgeGoal/delJudgeGoal")
    public Boolean delJudgeGoal(@RequestBody @Validated DelJudgeGoalRequest delJudgeGoal ) {
        return judgeGoalBiz.delJudgeGoal(delJudgeGoal);
    }

    /**
     * 删除关联指标
     * @param delRefIndicator
     * @return
     */
    @Operation(summary = "删除公式")
    @DeleteMapping("v1/baseIndicator/JudgeGoal/delRefExpression")
    public Boolean delRefExpression(@RequestBody @Validated DelRefIndicatorRequest delRefIndicator ) {
        return judgeGoalBiz.delRefExpression(delRefIndicator);
    }
}
