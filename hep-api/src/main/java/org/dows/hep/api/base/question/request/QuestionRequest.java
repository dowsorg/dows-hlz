package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "Question 对象", title = "问题Request")
public class QuestionRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "类别Id")
    private String categId;

    @Schema(title = "维度ID")
    private String dimensionId;

    @Schema(title = "题目答题输入类型[input,select,text]")
    private String inputType;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "答案解析")
    private String detailedAnswer;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "选项集合")
    private String QuestionOptionsRequest;

    @Schema(title = "问题答案")
    private String QuestionAnswersRequest;

    @Schema(title = "子问题")
    private String QuestionRequest;


}
