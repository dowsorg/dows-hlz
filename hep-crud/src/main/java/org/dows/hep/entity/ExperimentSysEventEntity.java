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
 * 实验系统事件(ExperimentSysEvent)实体类
 *
 * @author wuzl
 * @since 2023-08-22 09:31:00
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentSysEvent", title = "实验系统事件")
@TableName("experiment_sys_event")
public class ExperimentSysEventEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验系统事件ID")
    private String experimentSysEventId;

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
    @Schema(title = "事件类型 1-方案截止 2-期初通知 3-期末翻转 9-排行榜")
    private Integer eventType;

    @Schema(title = "触发类型 1-方案截止 2-期初 3-期末")
    private Integer triggerType ;

    @Schema(title = "触发中时间")
    private Date triggeringTime;

    @Schema(title = "触发中游戏内天数")
    private Integer triggeringGameDay;

    @Schema(title = "触发时间")
    private Date triggeredTime;
    @Schema(title = "触发期数")
    private Integer triggeredPeriod;

    @Schema(title = "触发游戏内天数")
    private Integer triggeredGameDay;

    @Schema(title = "处理序号")
    private Integer dealSeq;

    @Schema(title = "'处理次数'")
    private Integer dealTimes;
    @Schema(title = "处理时间")
    private Date dealTime;

    @Schema(title = "处理消息")
    private String dealMsg;
    @Schema(title = "事件内容")
    private String context;
    @Schema(title = "事件状态 0-初始 1-已触发 2-已处理")
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

