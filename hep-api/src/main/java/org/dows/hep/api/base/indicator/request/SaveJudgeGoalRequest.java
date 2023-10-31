package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:49
 */

@Data
@NoArgsConstructor
@Schema(name = "SaveJudgeGoal 对象", title = "保存管理目标")
public class SaveJudgeGoalRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "数据库id，新增时为空")
    private Long id;

    @Schema(title = "管理目标id")
    private String indicatorJudgeGoalId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "分类ID")
    private String categId;

    @Schema(title = "管理目标名称")
    private String indicatorJudgeGoalName;

    @Schema(title = "状态 0-停用 1-启用")
    private Integer state;

    @Schema(title = "判断规则公式id列表")
    private List<String> judgeRuleExpresssions;
    @Schema(title = "关联指标公式id列表")
    private List<IndicatorExpressionVO> effectExpresssions;

    @Schema(title = "值类型 0-字符串 1-整数 2-小数")
    private Integer valueType;
}
