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
 * 实验任务调度(ExperimentTaskSchedule)实体类
 *
 * @author lait
 * @since 2023-07-13 16:03:43
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentTaskSchedule", title = "实验任务调度")
@TableName("experiment_task_schedule")
public class ExperimentTaskScheduleEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验任务计时器ID")
    private String experimentTaskTimerId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "任务beancode")
    private String taskBeanCode;

    @Schema(title = "任务参数")
    private String taskParams;

    @Schema(title = "执行时间表达式")
    private String executeExpression;

    @Schema(title = "应用ID")
    private String appId;


    @Schema(title = "开始执行时间")
    private Date executeTime;

    @Schema(title = "启动时间")
    private Date restartTime;

    @Schema(title = "是否已执行")
    private Boolean executed;


    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

