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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author : wuzl
 * @date : 2023/10/21 15:05
 */

@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentJudgeScoreLog", title = "学生判断得分记录")
@TableName("experiment_judge_score_log")
public class ExperimentJudgeScoreLogEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "得分记录id")
    private String experimentJudgeScoreLogId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验操作流程id")
    private String operateFlowId;

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

    @Schema(title = "期数")
    private Integer period;
    @Schema(title = "操作得分")
    private BigDecimal score;

    @Schema(title = "单组操作得分")
    private BigDecimal singleScore;

    @Schema(title = "操作得分详情")
    private String scoreJson;

    @Schema(title = "操作时间")
    private Date operateTime;

    @Schema(title = "操作所在游戏内天数")
    private Integer operateGameDay;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;




}
