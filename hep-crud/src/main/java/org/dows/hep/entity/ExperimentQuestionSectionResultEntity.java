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
 * 实验问题集[试卷]-答题结果记录(ExperimentQuestionSectionResult)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:40
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentQuestionSectionResult", title = "实验问题集[试卷]-答题结果记录")
@TableName("experiment_question_section_result")
public class ExperimentQuestionSectionResultEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验问题集[试卷]-答题结果记录Id")
    private String experimentQuestionSectionResultId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "组员id")
    private String accountId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "答题结果记录ID")
    private String questionSectionResultId;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

