package org.dows.hep.rest.user.experiment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.ExperimentPersonHealthProblemRequest;
import org.dows.hep.api.base.indicator.response.ExperimentPersonHealthProblemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgJudgeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     *
     * 获取二级类无报告的判断指标信息
     * @param
     * @return
     */
    @Operation(summary = "获取二级类无报告的判断指标信息")
    @PostMapping("v1/userExperiment/experimentOrgJudge/getIndicatorJudgeRiskFactor")
    public Map<String,List<IndicatorJudgeRiskFactorResponse>> getIndicatorJudgeRiskFactor(@RequestParam @Validated String indicatorFuncId) {
        return experimentOrgJudgeBiz.getIndicatorJudgeRiskFactor(indicatorFuncId);
    }

    /**
     *
     * 获取二级类有报告的判断指标信息
     *
     * @param
     * @return
     */
    @Operation(summary = "获取二级类有报告的判断指标信息")
    @PostMapping("v1/userExperiment/experimentOrgJudge/getIndicatorJudgeHealthGuidance")
    public Map<String,List<IndicatorJudgeHealthGuidanceResponse>> getIndicatorJudgeHealthGuidance(@RequestParam @Validated String indicatorFuncId) {
        return experimentOrgJudgeBiz.getIndicatorJudgeHealthGuidance(indicatorFuncId);
    }

    /**
     *
     * 三级类别/四级类别：根据指标分类ID获取所有符合条件的数据
     * @param
     * @return
     */
    @Operation(summary = "三级类别/四级类别：根据指标分类ID获取所有符合条件的数据")
    @GetMapping("v1/userExperiment/experimentOrgJudge/getIndicatorJudgeHealthProblemByCategoryId/{indicatoryCategoryId}")
    public List<IndicatorJudgeHealthProblemResponse> getIndicatorJudgeHealthProblemByCategoryId(@PathVariable String indicatoryCategoryId) {
        return experimentOrgJudgeBiz.getIndicatorJudgeHealthProblemByCategoryId(indicatoryCategoryId);
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
     * 是否购买保险
     * @param
     * @return
     */
    @Operation(summary = "是否购买保险")
    @PostMapping("v1/userExperiment/experimentOrgJudge/isPurchaseInsure")
    public Boolean isPurchaseInsure(@RequestParam @Validated String isPurchase,
                                    @RequestParam @Validated String experimentPersonId)
    {
        return experimentOrgJudgeBiz.isPurchaseInsure(isPurchase,experimentPersonId);
    }

    /**
     * 二级-无报告 判断操作
     * @param
     * @return
     */
    @Operation(summary = "isIndicatorJudgeRiskFactor")
    @PostMapping("v1/userExperiment/experimentOrgJudge/isIndicatorJudgeRiskFactor")
    public Boolean isIndicatorJudgeRiskFactor(@RequestBody @Validated List<CreateIndicatorJudgeRiskFactorRequest> judgeRiskFactorRequestList)
    {
        return experimentOrgJudgeBiz.isIndicatorJudgeRiskFactor(judgeRiskFactorRequestList);
    }

    /**
     * 三级-无报告 保存操作
     * @param
     * @return
     */
    @Operation(summary = "saveExperimentIndicatorJudgeHealthProblem")
    @PostMapping("v1/userExperiment/experimentOrgJudge/saveExperimentIndicatorJudgeHealthProblem")
    public Boolean saveExperimentIndicatorJudgeHealthProblem(@RequestBody @Validated List<CreateIndicatorJudgeHealthProblemRequest> judgeHealthProblemRequestList)
    {
        return experimentOrgJudgeBiz.saveExperimentIndicatorJudgeHealthProblem(judgeHealthProblemRequestList);
    }

    /**
     * 三级-无报告 获取分页
     * @param
     * @return
     */
    @Operation(summary = "pageExperimentIndicatorJudgeHealthProblem")
    @PostMapping("v1/userExperiment/experimentOrgJudge/pageExperimentIndicatorJudgeHealthProblem")
    public IPage<ExperimentPersonHealthProblemResponse> pageExperimentIndicatorJudgeHealthProblem(@RequestBody @Validated ExperimentPersonHealthProblemRequest experimentPersonHealthProblemRequest)
    {
        return experimentOrgJudgeBiz.pageExperimentIndicatorJudgeHealthProblem(experimentPersonHealthProblemRequest);
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