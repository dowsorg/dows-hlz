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
 * 学生随访操作计时器(OperateFollowupTimer)实体类
 *
 * @author lait
 * @since 2023-04-28 10:27:00
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateFollowupTimer", title = "学生随访操作计时器")
@TableName("operate_followup_timer")
public class OperateFollowupTimerEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "随访操作计时器id")
    private String operateFollowupTimerId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "案例账号ID")
    private String caseAccountId;

    @Schema(title = "账号名称")
    private String caseAccountName;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "随访表id")
    private String indicatorViewMonitorFollowupId;

    @Schema(title = "随访表名称")
    private String indicatorFollowupName;

    @Schema(title = "游戏内起始天数")
    private Integer startDay;

    @Schema(title = "随访间隔天数")
    private Integer dueDays;

    @Schema(title = "可以随访时间")
    private Integer todoDay;

    @Schema(title = "上次随访时间")
    private Integer doneDay;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @Schema(title = "最近保存时间")
    private Date setTime;

    @Schema(title = "最近随访时间")
    private Date followupTime;

}

