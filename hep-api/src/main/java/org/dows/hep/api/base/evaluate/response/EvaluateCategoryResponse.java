package org.dows.hep.api.base.evaluate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "EvaluateCategResponse 对象", title = "评估问卷类目Response")
public class EvaluateCategoryResponse {
    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "类别父id")
    private String evaluateCategPid;

    @Schema(title = "类别组")
    private String evaluateCategGroup;

    @Schema(title = "类别名")
    private String evaluateCategName;

    @Schema(title = "序列号")
    private Integer sequence;

    @Schema(title = "子")
    private List<EvaluateCategoryResponse> children;
}
