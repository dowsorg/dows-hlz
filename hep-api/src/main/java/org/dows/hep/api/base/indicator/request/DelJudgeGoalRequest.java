package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:50
 */

@Data
@NoArgsConstructor
@Schema(name = "DelSpotItem 对象", title = "删除管理目标")
public class DelJudgeGoalRequest {

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "分布式id列表")
    @ApiModelProperty(required = true)
    private List<String> ids;
}
