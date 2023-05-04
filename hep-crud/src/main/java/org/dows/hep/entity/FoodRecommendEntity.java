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
 * 食物推荐量配置(FoodRecommend)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:05
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "FoodRecommend", title = "食物推荐量配置")
@TableName("food_recommend")
public class FoodRecommendEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String foodRecommendId;

    @Schema(title = "主体类型,1-营养成分 2-食材一级分类")
    private Boolean instanceType;

    @Schema(title = "主体id ")
    private String instanceId;

    @Schema(title = "主体名称")
    private String instanceName;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "推荐量下限")
    private String min;

    @Schema(title = "推荐量上限")
    private String max;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

