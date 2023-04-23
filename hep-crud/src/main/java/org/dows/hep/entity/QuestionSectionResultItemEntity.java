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
 * 问题集[试卷]-答题记录Item(QuestionSectionResultItem)实体类
 *
 * @author lait
 * @since 2023-04-18 13:59:32
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "QuestionSectionResultItem", title = "问题集[试卷]-答题记录Item")
@TableName("question_section_result_item")
public class QuestionSectionResultItemEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "记录项ID")
    private String questionSectionResultItemId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "问题集维度ID")
    private String questionSectionDimensionId;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "正确答案[JSON]")
    private String rightValue;

    @Schema(title = "答案值ID[JSON]")
    private String answerId;

    @Schema(title = "答题值[JSON]")
    private String answerValue;

    @Schema(title = "是否正确[0：错误|1：一半|2：完成正确]")
    private Boolean right;

    @Schema(title = "分数")
    private Float score;

    @Schema(title = "问题集标识")
    private String questionSectionIdentifier;

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

