package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.user.experiment.response.ExptTreatPlanResponse;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/17 17:51
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptOrgReportNodeDataVO 对象", title = "机构挂号报告节点数据")
public class ExptOrgReportNodeDataVO {

    @Schema(title = "基本信息")
    private ExperimentIndicatorViewBaseInfoRsResponse viewBaseInfo;

    @Schema(title = "查看指标二级--体格检查")
    private List<ExperimentPhysicalExamReportResponseRs> viewTwoLevel;

    @Schema(title = "查看指标四级--辅助检查")
    private List<ExperimentSupportExamReportResponseRs> viewFourLevel;

    @Schema(title = "操作指标二级--心理治疗")
    private ExptTreatPlanResponse treatTwoLevel;
    @Schema(title = "操作指标四级--药物治疗")
    private ExptTreatPlanResponse treatFourLevel;

    @Schema(title = "判断指标--风险因素")
    private List<ExperimentRiskFactorReportResponseRs> judgeRiskFactor;

    @Schema(title = "判断指标--健康问题")
    private List<ExperimentHealthProblemReportResponseRs> judgeHealthProblem;

    @Schema(title = "判断指标--健康指导")
    private List<ExperimentHealthGuidanceReportResponseRs> judgeHealthGuidance;

 /*   @Schema(title = "判断指标--疾病问题")
    private Object judgeDiseaseProblem;

    @Schema(title = "判断指标--健管目标")
    private Object judgeHealthGoal;*/






}
