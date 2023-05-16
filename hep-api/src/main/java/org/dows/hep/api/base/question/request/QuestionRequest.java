package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;

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
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题ID-更新需要")
    private String questionInstanceId;

    @Schema(title = "题目类型ID")
    private String questionCategId;

    @Schema(title = "题目答题类型[RADIO_SELECT:单选题|MULTIPLE_SELECT:多选题|JUDGMENT:判断题|SUBJECTIVE:主观题|MATERIAL:材料题]")
    private QuestionTypeEnum questionType;

    @Schema(title = "维度ID")
    private String dimensionId;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "状态 0-禁用 1-启用")
    private Integer enabled;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "答案解析")
    private String detailedAnswer;

    @Schema(title = "选项和答案集合")
    private List<QuestionOptionWithAnswerRequest> optionWithAnswerList;

    @Schema(title = "子问题")
    private List<QuestionRequest> children;



    // JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonIgnore
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "问题PID")
    @JsonIgnore
    private String questionInstancePid;

    @Schema(title = "来源")
    @JsonIgnore
    private String source;

    @Schema(title = "引用计数")
    @JsonIgnore
    private Integer refCount;

    @Schema(title = "问题标识")
    @JsonIgnore
    private String questionIdentifier;

    @Schema(title = "版本号")
    @JsonIgnore
    private String ver;

    @Schema(title = "biz code: PUBLIC_VIEWING：被所有人查看 | PRIVATE_VIEWING：只能被自己查看")
    @JsonIgnore
    private QuestionAccessAuthEnum bizCode;

}
