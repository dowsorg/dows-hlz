package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/10/18 15:13
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "JudgeGoal 对象", title = "管理目标列表")
public class JudgeGoalResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String indicatorJudgeGoalId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;


    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "管理目标名称")
    private String indicatorJudgeGoalName;

    @Schema(title = "状态 0-停用 1-启用")
    private Integer state;

    @Schema(title = "值类型 0-字符串 1-整数 2-小数")
    private Integer valueType;

}
