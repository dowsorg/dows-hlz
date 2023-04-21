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
 * 事件处理选项影响指标(EventActionIndicator)实体类
 *
 * @author lait
 * @since 2023-04-21 19:41:30
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EventActionIndicator", title = "事件处理选项影响指标")
@TableName("event_action_indicator")
public class EventActionIndicatorEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String eventActionIndicatorId;

    @Schema(title = "事件id")
    private String eventId;

    @Schema(title = "初始指标影响标记，0-否 1-是")
    private Boolean initFlag;

    @Schema(title = "事件选项id")
    private String eventActionId;

    @Schema(title = "指标id")
    private String indicatorInstaceId;

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

