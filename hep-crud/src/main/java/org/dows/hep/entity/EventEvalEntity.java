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
 * 突发事件触发条件(EventEval)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:46
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EventEval", title = "突发事件触发条件")
@TableName("event_eval")
public class EventEvalEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String eventEvalId;

    @Schema(title = "事件id")
    private String eventId;

    @Schema(title = "表达式")
    private String expression;

    @Schema(title = "触发条件描述")
    private String expressionDescr;

    @Schema(title = "表达式涉及变量")
    private String expressionVars;

    @Schema(title = "最小值")
    private String expressionMin;

    @Schema(title = "最大值")
    private String expressionMax;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

