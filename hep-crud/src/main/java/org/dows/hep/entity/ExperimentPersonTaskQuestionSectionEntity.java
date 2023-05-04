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
 * 实验人物问题集(ExperimentPersonTaskQuestionSection)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:39
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentPersonTaskQuestionSection", title = "实验人物问题集")
@TableName("experiment_person_task_question_section")
public class ExperimentPersonTaskQuestionSectionEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物问题集Id")
    private String experimentPersonTaskQuestionSection;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验人物 ID")
    private String experimentPersonId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "组员id")
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

