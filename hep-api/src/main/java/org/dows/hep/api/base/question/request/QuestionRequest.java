package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Question 对象", title = "问题Request")
public class QuestionRequest {
    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "题目类型ID")
    @NotBlank(message = "题目类别不能为空")
    private String questionCategId;

    @Schema(title = "题目答题类型[RADIO_SELECT:单选题|MULTIPLE_SELECT:多选题|JUDGMENT:判断题|SUBJECTIVE:主观题|MATERIAL:材料题]")
    @NotBlank(message = "选择题型不能为空")
    private String questionType;

    @Schema(title = "维度ID-单维度情况")
    private String dimensionId;

    @Schema(title = "维度ID-多维度情况")
    private List<String> dimensionIds;

    @Schema(title = "问题标题")
    @NotBlank(message = "问题标题不能为空")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "状态 0-禁用 1-启用")
    private Integer enabled;

    @Schema(title = "答案解析")
    @Min(0)
    @Max(300)
    private String detailedAnswer;

    @Schema(title = "选项和答案集合")
    private List<QuestionOptionWithAnswerRequest> optionWithAnswerList;

    @Schema(title = "子问题")
    private List<QuestionRequest> children;

    @Schema(title = "创建者账号ID")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者Name")
    @JsonIgnore
    private String accountName;

}
