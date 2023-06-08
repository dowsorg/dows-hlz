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

import java.util.Date;

@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentEvaluateQuestionnaireItem", title = "实验评估问卷item")
@TableName("experiment_evaluate_questionnaire_item")
public class ExperimentEvaluateQuestionnaireItemEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "item id")
    private String experimentEvaluateQuestionnaireItemId;

    @Schema(title = "pid")
    private String experimentEvaluateQuestionnaireItemPid;

    @Schema(title = "实验评估问卷ID")
    private String experimentEvaluateQuestionnaireId;

    @Schema(title = "问题题目")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "问题选项")
    private String questionOptions;

    @Schema(title = "问题详解")
    private String questionDetailedAnswer;

    @Schema(title = "正确答案")
    private String rightValue;

    @Schema(title = "排序")
    private String seq;

    @Schema(title = "问题结果")
    private String questionResult;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
