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
import  org.dows.framework.crud.mybatis.CrudEntity;

/**
 * 问题-答案(QuestionAnswers)实体类
 *
 * @author lait
 * @since 2023-04-18 13:58:58
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "QuestionAnswers", title = "问题-答案")
@TableName("question_answers")
public class QuestionAnswersEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "")
    private String q;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "选项ID")
    private String questionOptionsId;

    @Schema(title = "选项标题")
    private String optionTitle;

    @Schema(title = "问题的答案")
    private String optionValue;

    @Schema(title = "是否是正确答案[0:错误，1:正确]")
    private Boolean right;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

