package org.dows.hep.api.base.evaluate.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/5/24 10:57
 */
@Data
@NoArgsConstructor
@Schema(name = "EvaluateCategRequest 对象", title = "评估问卷类目Request")
public class EvaluateCategoryRequest {
    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "类别组")
    @NotBlank(message = "类别组不能为空")
    private String evaluateCategGroup;

    @Schema(title = "类别名")
    @NotBlank(message = "类别名不能为空")
    private String evaluateCategName;

    @Schema(title = "序列号")
    private Integer sequence;
}
