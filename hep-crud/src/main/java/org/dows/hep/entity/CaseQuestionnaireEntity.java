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
 * 案例问卷(CaseQuestionnaire)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:46
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseQuestionnaire", title = "案例问卷")
@TableName("case_questionnaire")
public class CaseQuestionnaireEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "期数")
    private String periods;

    @Schema(title = "期数排序")
    private Boolean periodSequence;

    @Schema(title = "分配方式")
    private String allotMode;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "题型结构")
    private String questionSectionStructure;

    @Schema(title = "案例标示")
    private String caseIdentifier;

    @Schema(title = "版本号")
    private String ver;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

