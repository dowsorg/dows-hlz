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
 * 查看指标监测随访类(IndicatorViewMonitorFollowup)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:33
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewMonitorFollowupRsEntity", title = "查看指标监测随访类")
@TableName("experiment_indicator_view_monitor_followup_rs")
public class ExperimentIndicatorViewMonitorFollowupRsEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String experimentIndicatorViewMonitorFollowupId;

    @Schema(title = "分布式ID")
    private String indicatorViewMonitorFollowupId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标功能ID")
    private String indicatorFuncId;

    @Schema(title = "指标监测随访类表名称")
    private String name;

    @Schema(title = "监测随访表类别Id")
    private String indicatorCategoryId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

