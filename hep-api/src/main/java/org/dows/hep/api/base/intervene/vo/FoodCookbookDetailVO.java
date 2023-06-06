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
 * @date : 2023/5/6 9:45
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodDetailVO 对象", title = "食物明细")

public class FoodCookbookDetailVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;
    @Schema(title = "关联分布式id，删除时使用")
    private String refId;


    @Schema(title = "进餐时间，1-早|2-早加|3-午|4-午加|5-晚|6-晚加")
    @ApiModelProperty(required = true)
    private Integer mealTime;

    @Schema(title = "明细类型，1-食材 2-菜肴 ")
    @ApiModelProperty(required = true)
    private Integer instanceType;

    @Schema(title = "主体(食材，菜肴)id")
    @ApiModelProperty(required = true)
    private String instanceId;

    @Schema(title = "菜肴、食材名称")
    @ApiModelProperty(required = true)
    private String instanceName ;

    @Schema(title = "食材含量描述")
    private String materialsDesc;

    @Schema(title = "重量")
    @ApiModelProperty(required = true)
    private String weight;



}
