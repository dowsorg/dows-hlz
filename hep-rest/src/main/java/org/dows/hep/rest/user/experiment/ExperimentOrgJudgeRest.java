package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgJudgeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:实验:机构操作-判断指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构操作-判断指标", description = "机构操作-判断指标")
public class ExperimentOrgJudgeRest {
    private final ExperimentOrgJudgeBiz experimentOrgJudgeBiz;

    /**
    * 健康问题+健康指导：获取问题列表（含分类）
    * @param
    * @return
    */
    @Operation(summary = "健康问题+健康指导：获取问题列表（含分类）")
    @PostMapping("v1/userExperiment/experimentOrgJudge/listOrgJudgeItems")
    public List<OrgJudgeItemsResponse> listOrgJudgeItems(@RequestBody @Validated FindOrgJudgeItemsRequest findOrgJudgeItems ) {
        return experimentOrgJudgeBiz.listOrgJudgeItems(findOrgJudgeItems);
    }

    /**
    * 疾病问题：获取检查类别+项目
    * @param
    * @return
    */
    @Operation(summary = "疾病问题：获取检查类别+项目")
    @PostMapping("v1/userExperiment/experimentOrgJudge/listOrgJudgeCategs")
    public List<OrgJudgeCategResponse> listOrgJudgeCategs(@RequestBody @Validated FindOrgJudgeCategsRequest findOrgJudgeCategs ) {
        return experimentOrgJudgeBiz.listOrgJudgeCategs(findOrgJudgeCategs);
    }

    /**
    * 健康问题+健康指导+疾病问题：获取最新保存列表
    * @param
    * @return
    */
    @Operation(summary = "健康问题+健康指导+疾病问题：获取最新保存列表")
    @PostMapping("v1/userExperiment/experimentOrgJudge/listOrgJudgedItems")
    public List<OrgJudgedItemsResponse> listOrgJudgedItems(@RequestBody @Validated FindOrgJudgedItemsRequest findOrgJudgedItems ) {
        return experimentOrgJudgeBiz.listOrgJudgedItems(findOrgJudgedItems);
    }

    /**
    * 健康问题+健康指导+疾病问题：保存
    * @param
    * @return
    */
    @Operation(summary = "健康问题+健康指导+疾病问题：保存")
    @PostMapping("v1/userExperiment/experimentOrgJudge/saveOrgJudge")
    public SaveOrgJudgeResponse saveOrgJudge(@RequestBody @Validated SaveOrgJudgeRequest saveOrgJudge ) {
        return experimentOrgJudgeBiz.saveOrgJudge(saveOrgJudge);
    }

    /**
    * 健管目标：获取健管目标列表
    * @param
    * @return
    */
    @Operation(summary = "健管目标：获取健管目标列表")
    @PostMapping("v1/userExperiment/experimentOrgJudge/listOrgJudgeGoals")
    public OrgJudgeGoalsResponse listOrgJudgeGoals(@RequestBody @Validated FindOrgJudgeGoalsRequest findOrgJudgeGoals ) {
        return experimentOrgJudgeBiz.listOrgJudgeGoals(findOrgJudgeGoals);
    }

    /**
    * 健管目标：保存，包含是否购买保险
    * @param
    * @return
    */
    @Operation(summary = "健管目标：保存，包含是否购买保险")
    @PostMapping("v1/userExperiment/experimentOrgJudge/saveOrgJudgeGoals")
    public SaveOrgJudgeGoalsResponse saveOrgJudgeGoals(@RequestBody @Validated SaveOrgJudgeGoalsRequest saveOrgJudgeGoals ) {
        return experimentOrgJudgeBiz.saveOrgJudgeGoals(saveOrgJudgeGoals);
    }


}