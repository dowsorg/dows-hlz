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

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @Schema(title = "指标功能点id")
    private String indicatorFuncId;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "实验随访表id")
    private String experimentViewMonitorFollowupId;

    @Schema(title = "实验随访表名称")
    private String experimentFollowupName;

    @Schema(title = "实验截止时间")
    private Date experimentDeadline;

    @Schema(title = "是否挂号")
    private Boolean isRegister;

    @Schema(title = "随访间隔天数")
    private Integer dueDays;

    @Schema(title = "可以随访时间")
    private Date todoTime;

    @Schema(title = "最近保存时间")
    private Date setAtTime;

    @Schema(title = "最近随访时间")
    private Date followupTime;

    @Schema(title = "实验随访次数")
    private Integer followupTimes;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}

