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
 * 指标值(IndicatorVal)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:49
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseIndicatorValEntity", title = "案例指标值")
@TableName("case_indicator_val")
public class CaseIndicatorValEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "案例分布式ID")
    private String caseIndicatorValId;

    @Schema(title = "分布式ID")
    private String indicatorValId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例指标ID")
    private String caseIndicatorInstanceId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "案例当前值")
    private String caseCurrentVal;

    @Schema(title = "当前值")
    private String currentVal;

    @Schema(title = "最小值")
    private String caseMin;

    @Schema(title = "最大值")
    private String caseMax;

    @Schema(title = "默认值")
    private String caseDef;

    @Schema(title = "描述")
    private String caseDescr;

    @Schema(title = "期数")
    private String casePeriods;

    @Schema(title = "最小值")
    private String min;

    @Schema(title = "最大值")
    private String max;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "描述")
    private String descr;

    @Schema(title = "期数")
    private String periods;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

