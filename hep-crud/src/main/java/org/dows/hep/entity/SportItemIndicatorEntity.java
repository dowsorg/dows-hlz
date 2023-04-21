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
 * 运动项目关联指标(SportItemIndicator)实体类
 *
 * @author lait
 * @since 2023-04-21 19:41:33
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SportItemIndicator", title = "运动项目关联指标")
@TableName("sport_item_indicator")
public class SportItemIndicatorEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "关联id")
    private String sportItemIndicatorId;

    @Schema(title = "运动项目id")
    private String sportItemId;

    @Schema(title = "分布式ID")
    private String indicatorInstanceId;

    @Schema(title = "表达式")
    private String expression;

    @Schema(title = "公式描述")
    private String expressionDescr;

    @Schema(title = "表达式涉及变量")
    private String expressionVars;

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

