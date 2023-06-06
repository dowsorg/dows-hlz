package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/5/5 17:19
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodMaterialVO 对象", title = "食材组成")

public class FoodMaterialVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;

    @Schema(title = "关联分布式id，删除使用")
    private String refId;

    @Schema(title = "食材id")
    @ApiModelProperty(required = true)
    private String foodMaterialId;

    @Schema(title = "食材名称")
    @ApiModelProperty(required = true)
    private String foodMaterialName;

    @Schema(title = "重量")
    @ApiModelProperty(required = true)
    private String weight;

}
