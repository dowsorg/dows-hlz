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

import java.util.Date;

/**
 * 评估类别管理(EvaluateCategEntity)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:18
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EvaluateCategoryEntity", title = "评估类目管理")
@TableName("evaluate_category")
public class EvaluateCategoryEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "类别父id")
    private String evaluateCategPid;

    @Schema(title = "类别名")
    private String evaluateCategName;

    @Schema(title = "类别组")
    private String evaluateCategGroup;

    @Schema(title = "序列号")
    private Integer sequence;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
