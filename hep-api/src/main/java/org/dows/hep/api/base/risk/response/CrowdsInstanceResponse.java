package org.dows.hep.api.base.risk.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/15 15:15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CrowdsInstance 对象", title = "人群类别")
public class CrowdsInstanceResponse {

    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "人群类别分布式ID")
    private String crowdsId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "人群类别名称")
    private String name;

    @Schema(title = "概率")
    private Integer odds;

    @Schema(title = "人群公式ID")
    private Integer crowdsFormulaId;
}
