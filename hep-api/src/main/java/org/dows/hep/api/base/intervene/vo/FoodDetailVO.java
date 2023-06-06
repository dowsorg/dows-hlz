package org.dows.hep.api.base.intervene.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/5/6 9:45
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodDetailVO 对象", title = "食物明细")

public class FoodDetailVO {

    @Schema(title = "明细类型，1-食材 2-菜肴 ")
    @ApiModelProperty(required = true)
    private Integer instanceType;

    @Schema(title = "主体(食材，菜肴)id")
    @ApiModelProperty(required = true)
    private String instanceId;

    @Schema(title = "主体名称")
    @ApiModelProperty(required = true)
    private String instanceName ;

    @Schema(title = "重量")
    @ApiModelProperty(required = true)
    private String weight;



}
