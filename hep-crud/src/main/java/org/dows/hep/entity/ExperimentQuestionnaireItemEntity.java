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
@Schema(name = "ExperimentQuestionnaireItem", title = "实验知识答题Item")
@TableName("experiment_questionnaire_item")
public class ExperimentQuestionnaireItemEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "item id")
    private String experimentQuestionnaireItemId;

    @Schema(title = "pid")
    private String experimentQuestionnaireItemPid;

    @Schema(title = "实验知识答题Id")
    private String experimentQuestionnaireId;

    @Schema(title = "问题类别")
    private String questionCateg;

    @Schema(title = "问题类型")
    private String questionType;

    @Schema(title = "问题题目")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "问题选项")
    private String questionOptions;

    @Schema(title = "答案详情")
    private String questionDetailedAnswer;

    @Schema(title = "正确答案")
    private String rightValue;

    @Schema(title = "排序")
    private Integer seq;

    @Schema(title = "问题答案")
    private String questionResult;

    @Schema(title = "得分等级 0-错误 1-对一半 2-全对")
    private Integer scoreGrade;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}
