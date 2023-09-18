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
 * @author : wuzl
 * @date : 2023/9/2 18:46
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentFollowupPlan", title = "学生随访计划")
@TableName("experiment_followup_plan")
public class ExperimentFollowupPlanEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "随访计划id")
    private String experimentFollowupPlanId;

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

    @Schema(title = "挂号流水号")
    private String operateFlowId;

    @Schema(title = "实验随访表id")
    private String indicatorFollowupId;

    @Schema(title = "实验随访表名称")
    private String indicatorFollowupName;

    @Schema(title = "随访间隔天数")
    private Integer dueDays;
    @Schema(title = "最近保存所在天数")
    private Integer setAtDay;

    @Schema(title = "下次可随访天数")
    private Integer todoDay;

    @Schema(title = "已触发随访天数")
    private Integer doneDay;

    @Schema(title = "最近保存时间")
    private Date setAtTime;


    @Schema(title = "待触发随访时间")
    private Date doingTime;

    @Schema(title = "已触发随访时间")
    private Date doneTime;

    @Schema(title = "已触发随访次数")
    private Integer doneTimes;

    @Schema(title = "最近随访时间")
    private Date followupTime;

    @Schema(title = "已随访次数")
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
