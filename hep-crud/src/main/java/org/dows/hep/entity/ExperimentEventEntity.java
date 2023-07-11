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
 * 实验事件(ExperimentEvent)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:55
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentEvent", title = "实验事件")
@TableName("experiment_event")
public class ExperimentEventEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验事件ID")
    private String experimentEventId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "案例人物id")
    private String casePersonId;

    @Schema(title = "案例事件ID")
    private String caseEventId;

    @Schema(title = "人物名称")
    private String personName;

    @Schema(title = "触发类型 0-条件触发 1-第一期 2-第二期...5-第5期")
    private Integer triggerType;

    @Schema(title = "触发时间段 1-前期 2-中期 3-后期")
    private String triggerSpan;

    @Schema(title = "事件内容")
    private String eventJson;

    @Schema(title = "事件处理内容")
    private String actionJson;

    @Schema(title = "触发时间")
    private Date triggerTime;

    @Schema(title = "触发期数")
    private Integer triggeredPeriod;

    @Schema(title = "触发游戏内天数")
    private Integer triggerGameDay;

    @Schema(title = "处理时间")
    private Date actionTime;

    @Schema(title = "处理期数")
    private Integer actionPeriod;

    @Schema(title = "处理游戏内天数")
    private Integer actionGameDay;

    @Schema(title = "操作人ID")
    private String actionAccountId;

    @Schema(title = "操作人名")
    private String actionAccountName;

    @Schema(title = "事件状态 0-初始 1-已触发 2-用户已处理 3-系统已取消")
    private Integer state;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

