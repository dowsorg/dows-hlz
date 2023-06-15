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
 * 风险模型(RiskModel)实体类
 *
 * @author lait
 * @since 2023-04-28 10:29:05
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RiskModel", title = "风险模型")
@TableName("risk_model")
public class RiskModelEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "风险模型ID")
    private String riskModelId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "模型名称")
    private String name;

    @Schema(title = "死亡概率")
    private Integer riskDeathProbability;

    @Schema(title = "人群类别ID")
    private String crowdsCategoryId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "分数表达式")
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

