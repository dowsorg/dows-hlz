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
 * 指标监测随访随访内容表与指标关联关系(IndicatorViewMonitorFollowupContentRef)实体类
 *
 * @author lait
 * @since 2023-04-23 09:47:03
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "IndicatorViewMonitorFollowupContentRef", title = "指标监测随访随访内容表与指标关联关系")
@TableName("indicator_view_monitor_followup_content_ref")
public class IndicatorViewMonitorFollowupContentRefEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String indicatorViewMonitorFollowupContentRefId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标监测随访内容ID")
    private String indicatorViewMonitorFollowupFollowupContentId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "展示顺序")
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

