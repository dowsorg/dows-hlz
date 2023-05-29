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
 * 评估问卷(EvaluateQuestionnaire)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:19
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EvaluateQuestionnaire", title = "评估问卷")
@TableName("evaluate_questionnaire")
public class EvaluateQuestionnaireEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "问卷名")
    private String evaluateQuestionnaireName;

    @Schema(title = "问卷描述")
    private String evaluateQuestionnaireDesc;

    @Schema(title = "问题集")
    private String questionSectionId;

    @Schema(title = "操作提示")
    private String operationPrompt;

    @Schema(title = "对话提示")
    private String tips;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "创建者Name")
    private String accountName;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

