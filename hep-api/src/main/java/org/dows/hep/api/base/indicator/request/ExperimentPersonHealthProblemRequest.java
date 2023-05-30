package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/29 14:58
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentPersonHealthProblem 对象", title = "实验人物-判断操作-三级无报告")
public class ExperimentPersonHealthProblemRequest {
    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "pageNo")
    private Long pageNo;

    @Schema(title = "pageSize")
    private Long pageSize;
}
