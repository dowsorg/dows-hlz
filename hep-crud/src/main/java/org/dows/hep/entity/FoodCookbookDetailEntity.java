package org.dows.hep.entity;

import java.util.Date;

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

/**
 * 食谱食材(FoodCookbookDetail)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:47
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "FoodCookbookDetail", title = "食谱食材")
@TableName("food_cookbook_detail")
public class FoodCookbookDetailEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String foodCookbookDetailId;

    @Schema(title = "食谱id")
    private String foodCookbookId;

    @Schema(title = "进餐时间，早|早加|午|午加|晚|晚加")
    private String mealTime;

    @Schema(title = "明细类型，1-菜肴 2-食材")
    private String instanceType;

    @Schema(title = "菜肴、食材id")
    private String instanceId;

    @Schema(title = "菜肴、食材名称")
    private String instanceName;

    @Schema(title = "重量")
    private String weight;

    @Schema(title = "能量")
    private String energy;

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

