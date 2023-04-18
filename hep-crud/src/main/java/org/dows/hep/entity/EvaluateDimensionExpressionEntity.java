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
 * 评估维度公式(EvaluateDimensionExpression)实体类
 *
 * @author lait
 * @since 2023-04-18 13:54:33
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EvaluateDimensionExpression", title = "评估维度公式")
@TableName("evaluate_dimension_expression")
public class EvaluateDimensionExpressionEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String evaluateDimensionExpressionId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "评估问卷ID")
    private String questionnaireId;

    @Schema(title = "维度id")
    private String dimensionId;

    @Schema(title = "维度公式")
    private String expression;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

