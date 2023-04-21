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
 * 问题集[试卷]-答题记录(QuestionSectionResult)实体类
 *
 * @author lait
 * @since 2023-04-21 19:41:33
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "QuestionSectionResult", title = "问题集[试卷]-答题记录")
@TableName("question_section_result")
public class QuestionSectionResultEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "答题记录ID")
    private String questionSectionResultId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "问题集名称")
    private String questionSectionName;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "题型结构")
    private String questionSectionStructure;

    @Schema(title = "正确题数")
    private Integer rightCount;

    @Schema(title = "得分结构")
    private String scoreStructure;

    @Schema(title = "分数")
    private Object score;

    @Schema(title = "答题者账号Id")
    private String accountId;

    @Schema(title = "答题者姓名")
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

