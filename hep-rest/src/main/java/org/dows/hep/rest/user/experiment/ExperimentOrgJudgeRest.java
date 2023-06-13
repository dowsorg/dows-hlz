package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.util.JwtUtil;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.enums.EnumToken;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgJudgeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Map<String,List<ExperimentIndicatorJudgeRiskFactorResponse>> getIndicatorJudgeRiskFactor(@RequestParam @Validated String experimentIndicatorFuncId) {
        return experimentOrgJudgeBiz.getIndicatorJudgeRiskFactor(experimentIndicatorFuncId);
    }

    /**
     * 二级-无报告 获取判断得分
     * @param
     * @return
     */
    @Operation(summary = "getJudgeRiskFactorScore")
    @PostMapping("v1/userExperiment/experimentOrgJudge/getJudgeRiskFactorScore")
    public BigDecimal getJudgeRiskFactorScore(@RequestBody @Validated List<ExperimentIndicatorJudgeRiskFactorRequest> judgeRiskFactorRequestList)
    {
        return experimentOrgJudgeBiz.getJudgeRiskFactorScore(judgeRiskFactorRequestList);
    }

    /**
     * 判断操作 保存
     * @param
     * @return
     */
    @Operation(summary = "saveExperimentJudgeOperate")
    @PostMapping("v1/userExperiment/experimentOrgJudge/saveExperimentJudgeOperate")
    public Boolean saveExperimentJudgeOperate(@RequestBody @Validated List<OperateOrgFuncRequest> operateOrgFuncRequest, HttpServletRequest request)
    {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, EnumToken.PROPERTIES_JWT_KEY.getStr());
        //1、获取登录账户和名称
        String accountId = map.get("accountId").toString();
        String accountName = map.get("accountName").toString();
        return experimentOrgJudgeBiz.saveExperimentJudgeOperate(operateOrgFuncRequest,accountId,accountName);
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
    public Map<String,List<ExperimentIndicatorJudgeHealthGuidanceResponse>> getIndicatorJudgeHealthGuidance(@RequestParam @Validated String experimentIndicatorFuncId) {
        return experimentOrgJudgeBiz.getIndicatorJudgeHealthGuidance(experimentIndicatorFuncId);
    }

    /**
     *
     * 三级类别：根据指标分类ID获取所有符合条件的数据
     * @param
     * @return
     */
    @Operation(summary = "三级类别：根据指标分类ID获取所有符合条件的数据")
    @PostMapping("v1/userExperiment/experimentOrgJudge/getIndicatorJudgeHealthProblemByCategoryIds")
    public List<ExperimentIndicatorJudgeHealthProblemResponse> getIndicatorJudgeHealthProblemByCategoryIds(@RequestBody Set<String> experimentIndicatoryCategoryIds) {
        return experimentOrgJudgeBiz.getIndicatorJudgeHealthProblemByCategoryIds(experimentIndicatoryCategoryIds);
    }

    /**
     *
     * 四级类别：根据指标分类ID获取所有符合条件的数据
     * @param
     * @return
     */
    @Operation(summary = "四级类别：根据指标分类ID获取所有符合条件的数据")
    @GetMapping("v1/userExperiment/experimentOrgJudge/getIndicatorJudgeDiseaseProblemByCategoryId/{indicatoryCategoryId}")
    public List<ExperimentIndicatorJudgeDiseaseProblemResponse> getIndicatorJudgeDiseaseProblemByCategoryId(@PathVariable String indicatoryCategoryId) {
        return experimentOrgJudgeBiz.getIndicatorJudgeDiseaseProblemByCategoryId(indicatoryCategoryId);
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
     * 直接判断 判断范围是否满足公式
     * @param
     * @return
     */
    @Operation(summary = "checkRangeMatchFormula")
    @PostMapping("v1/userExperiment/experimentOrgJudge/checkRangeMatchFormula")
    public Boolean checkRangeMatchFormula(@RequestBody @Validated CreateIndicatorJudgeHealthManagementGoalRequest request)
    {
        return experimentOrgJudgeBiz.checkRangeMatchFormula(request);
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