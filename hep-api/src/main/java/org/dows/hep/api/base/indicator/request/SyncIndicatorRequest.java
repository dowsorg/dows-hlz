package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/10/12 15:47
 */

@Data
@Accessors(chain = true)
@Schema(name = "SyncIndicator 对象", title = "一键同步")
public class SyncIndicatorRequest {

    @Schema(title = "appId")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "人物ID,同步单个人物/单个指标时必须")
    private String accountId;

    @Schema(title = "人物指标ID,同步单个指标时必须")
    private String caseIndicatorId;
}
