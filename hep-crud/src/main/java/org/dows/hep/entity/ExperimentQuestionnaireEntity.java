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
 * 实验知识答题(ExperimentQuestionnaire)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:44
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentQuestionnaire", title = "实验知识答题")
@TableName("experiment_questionnaire")
public class ExperimentQuestionnaireEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验知识答题ID")
    private String experimentQuestionnaireId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "答题卡ID")
    private String questionSectionResultId;

    @Schema(title = "期数|位置")
    private String periods;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验答题者ID")
    private String experimentAccountId;

    @Schema(title = "方案状态[0:未开始,1:进行中, 2:已提交]")
    private Integer state;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
