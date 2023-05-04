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
 * 实验人物任务(ExperimentPersonTask)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:37
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentPersonTask", title = "实验人物任务")
@TableName("experiment_person_task")
public class ExperimentPersonTaskEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物任务id")
    private String experimentPersonTaskId;

    @Schema(title = "实验人物 ID")
    private String experimentPersonId;

    @Schema(title = "案列任务ID")
    private String caseTaskId;

    @Schema(title = "任务名称")
    private String taskName;

    @Schema(title = "任务类型")
    private Integer taskType;

    @Schema(title = "任务状态[0：待接取，1：进行中，2：已完成 3：已超时 4:已失败]")
    private Integer taskState;

    @Schema(title = "触发方式 [0：系统分配 （常规任务触发）  1：居民上报 2：他人分配 （部门分配）]")
    private Integer triggerMode;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

