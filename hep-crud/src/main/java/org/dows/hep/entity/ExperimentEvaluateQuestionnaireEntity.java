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
@Schema(name = "ExperimentEvaluateQuestionnaire", title = "实验评估问卷")
@TableName("experiment_evaluate_questionnaire")
public class ExperimentEvaluateQuestionnaireEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验评估问卷ID")
    private String experimentEvaluateQuestionnaireId;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "评估问卷名")
    private String evaluateQuestionnaireName;

    @Schema(title = "评估问卷描述")
    private String evaluateQuestionnaireDescr;

    @Schema(title = "评估问卷操作提示")
    private String operationPrompt;

    @Schema(title = "评估问卷对话提示")
    private String tips;

    @Schema(title = "作答人账号ID")
    private String accountId;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
