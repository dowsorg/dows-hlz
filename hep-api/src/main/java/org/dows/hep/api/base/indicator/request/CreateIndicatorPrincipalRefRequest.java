package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CreateIndicatorPrincipalRef 对象", title = "创建指标主体关联关系")
public class CreateIndicatorPrincipalRefRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标值ID")
    private String indicatorValId;

    @Schema(title = "主体ID")
    private String principalId;


}
