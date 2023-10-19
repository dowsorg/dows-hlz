package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/10/19 10:23
 */

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptJudgeGoalItemVO 对象", title = "管理目标")

public class ExptJudgeGoalItemVO {

    @Schema(title = "管理目标id")
    private String indicatorJudgeGoalId;

    @Schema(title = "管理目标名称")
    private String indicatorJudgeGoalName;

    @Schema(title = "分类ID")
    private String categId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "目标值")
    private String value;
}
