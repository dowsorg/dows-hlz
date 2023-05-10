package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/4/23 19:09
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodNutrientVO 对象", title = "饮食关键指标")
public class FoodNutrientVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;
    @Schema(title = "营养指标id")
    private String indicatorInstanceId;

    @Schema(title = "营养成分名称")
    private String nutrientName;

    @Schema(title = "成分单位")
    private String unit;

    @Schema(title = "初始值")
    private String amt;

    @Schema(title = "当前值")
    private String weight;
    @Schema(title = "排序号")
    private Integer seq;
}
