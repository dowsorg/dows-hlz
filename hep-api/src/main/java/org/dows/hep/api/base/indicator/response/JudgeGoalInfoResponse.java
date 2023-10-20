package org.dows.hep.api.base.indicator.response;

/**
 * @author : wuzl
 * @date : 2023/10/18 15:14
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "JudgeGoalInfo 对象", title = "管理目标详情")
public class JudgeGoalInfoResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String indicatorJudgeGoalId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "分类ID")
    private String categId;

    @Schema(title = "分类名称")
    private String categName;


    @Schema(title = "管理目标名称")
    private String indicatorJudgeGoalName;

    @Schema(title = "状态 0-停用 1-启用")
    private Integer state;

    @Schema(title = "判断规则-指标公式")
    private List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList;

    @Schema(title = "关联指标-指标公式")
    private List<IndicatorExpressionResponseRs> effectExpresssions;


}
