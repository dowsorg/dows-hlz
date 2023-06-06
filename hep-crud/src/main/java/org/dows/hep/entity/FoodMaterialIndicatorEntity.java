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
 * 食材关联指标(FoodMaterialIndicator)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:59
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "FoodMaterialIndicator", title = "食材关联指标")
@TableName("food_material_indicator")
public class FoodMaterialIndicatorEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "关联id")
    private String foodMaterialIndicatorId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "食材id")
    private String foodMaterialId;

    @Schema(title = "指标id")
    private String indicatorInstanceId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "表达式")
    private String expression;

    @Schema(title = "公式描述")
    private String expressionDescr;

    @Schema(title = "参数id列表")
    private String expressionVars;

    @Schema(title = "参数名列表")
    private String expressionNames;

    @Schema(title = "最小值")
    private String expressionMin;

    @Schema(title = "最大值")
    private String expressionMax;

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

