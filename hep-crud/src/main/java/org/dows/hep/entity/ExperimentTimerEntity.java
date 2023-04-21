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
 * 实验计数计时器(ExperimentTimer)实体类
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
@Schema(name = "ExperimentTimer", title = "实验计数计时器")
@TableName("experiment_timer")
public class ExperimentTimerEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验定时器ID")
    private String experimentTimerId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "暂停时长[暂停结束时间-暂停起始时间]")
    private Long duration;

    @Schema(title = "实验开始时间")
    private Long startTime;

    @Schema(title = "实验结束时间[如果有暂停，需加暂停时长]")
    private Long endTime;

    @Schema(title = "期数[根据期数生成对应的计时记录]")
    private Integer periods;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "暂停次数[每次暂停++]")
    private Integer pauseCount;

    @Schema(title = "状态[0:未开始，1:进行中，2:已结束]")
    private Integer state;

    @Schema(title = "是否暂停")
    private Boolean paused;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

