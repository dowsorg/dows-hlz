package org.dows.hep.api.base.risk.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/15 14:38
 */
@Data
@NoArgsConstructor
@Schema(name = "CrowdsInstance 对象", title = "人群类别")
public class CrowdsInstanceRequest {

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
