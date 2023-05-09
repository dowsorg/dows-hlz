package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudEntity;

import java.util.Date;

/**
 * 食谱成分(FoodCookbookNutrient)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:52
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "FoodCookbookNutrient", title = "食谱成分")
@TableName("food_cookbook_nutrient")
public class FoodCookbookNutrientEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "关联id")
    private String foodCookbookNutrientId;

    @Schema(title = "食谱id")
    private String foodCookbookId;

    @Schema(title = "主体类型 1-营养成分 2-食材分类")
    private Integer instanceType;

    @Schema(title = "主体(营养指标，食材分类)id")
    private String instanceId;

    @Schema(title = "主体名称")
    private String instanceName ;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "重量")
    private String weight;

    @Schema(title = "实际能量")
    private String energy;

    @Schema(title = "推荐量下限")
    private String min;

    @Schema(title = "推荐量上限")
    private String max;

    @Schema(title = "排序号")
    private Integer seq;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

