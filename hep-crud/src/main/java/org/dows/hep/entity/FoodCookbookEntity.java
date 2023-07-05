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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 食谱(FoodCookbook)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:47
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "FoodCookbook", title = "食谱")
@TableName("food_cookbook")
public class FoodCookbookEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "食谱id")
    private String foodCookbookId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "食谱名称")
    private String foodCookbookName;

    @Schema(title = "食材含量描述")
    private String materialsDesc;

    @Schema(title = "分类id")
    private String interveneCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "蛋白质每100g")
    private BigDecimal protein;

    @Schema(title = "碳水每100g")
    private BigDecimal cho;

    @Schema(title = "脂肪每100g")
    private BigDecimal fat;

    @Schema(title = "总能量每100g")
    private BigDecimal energy;

    @Schema(title = "蛋白质能量占比")
    private String proteinEnergy;

    @Schema(title = "碳水能量占比")
    private String choEnergy;

    @Schema(title = "脂肪能量占比")
    private String fatEnergy;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "说明")
    private String descr;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

