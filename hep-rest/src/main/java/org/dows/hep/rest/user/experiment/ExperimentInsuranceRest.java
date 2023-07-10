package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentPersonInsuranceRequest;
import org.dows.hep.biz.user.experiment.ExperimentInsuranceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;

/**
 * @author jx
 * @date 2023/6/28 11:40
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验-保险", description = "实验-保险")
public class ExperimentInsuranceRest {
    private final ExperimentInsuranceBiz experimentInsuranceBiz;

    /**
     * 购买保险
     * @param
     * @return
     */
    @Operation(summary = "购买保险")
    @PostMapping("v1/userExperiment/experimentOrgJudge/isPurchaseInsure")
    public Boolean isPurchaseInsure(@RequestBody @Validated ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest)
    {
        return experimentInsuranceBiz.isPurchaseInsure(experimentPersonInsuranceRequest);
    }

    /**
     * 获取保险状态
     * @param
     * @return
     */
    @Operation(summary = "获取保险状态")
    @PostMapping("v1/userExperiment/experimentOrgJudge/checkInsureStatus")
    public Boolean checkInsureStatus(@RequestBody @Validated ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest) throws ParseException {
        return experimentInsuranceBiz.checkInsureStatus(experimentPersonInsuranceRequest);
    }

    /**
     * 每期单人计算医疗占比
     * @param
     * @return
     */
    @Operation(summary = "每期单人计算医疗占比")
    @PostMapping("v1/userExperiment/experimentOrgJudge/calculatePeriodsFee")
    public BigDecimal calculatePeriodsFee(@RequestBody @Validated ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest)
    {
        return experimentInsuranceBiz.calculatePeriodsFee(experimentPersonInsuranceRequest);
    }

    /**
     * 每期医疗占比得分
     * @param
     * @return
     */
    @Operation(summary = "每期医疗占比得分")
    @PostMapping("v1/userExperiment/experimentOrgJudge/calculatePeriodsScore")
    public BigDecimal calculatePeriodsScore(@RequestBody @Validated ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest)
    {
        return experimentInsuranceBiz.calculatePeriodsScore(experimentPersonInsuranceRequest);
    }

    /**
     * 每期小组的平均医疗得分
     * @param
     * @return
     */
    @Operation(summary = "每期小组的平均医疗得分")
    @PostMapping("v1/userExperiment/experimentOrgJudge/calculatePeriodsGroupScore")
    public BigDecimal calculatePeriodsGroupScore(@RequestBody @Validated ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest)
    {
        return experimentInsuranceBiz.calculatePeriodsGroupScore(experimentPersonInsuranceRequest);
    }

    /**
     * 每期医疗占比得分的平均值
     * @param
     * @return
     */
    @Operation(summary = "每期医疗占比得分的平均值")
    @PostMapping("v1/userExperiment/experimentOrgJudge/calculatePeriodsTotalScore")
    public Map<String,BigDecimal> calculatePeriodsTotalScore(@RequestBody @Validated ExperimentPersonInsuranceRequest experimentPersonInsuranceRequest)
    {
        return experimentInsuranceBiz.calculatePeriodsTotalScore(experimentPersonInsuranceRequest);
    }
}
