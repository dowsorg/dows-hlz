package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.QuestionTypeEnum;

import java.util.List;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "Question 对象", title = "问题Response")
public class QuestionResponse {
    @Schema(title = "问题ID-更新需要")
    private String questionInstanceId;

    @Schema(title = "题目类型ID")
    private String categId;

    @Schema(title = "题目答题类型[RADIO:单选题|MULTIPLE:多选题|JUDGMENT:判断题|SUBJECTIVE:主观题|MATERIAL:材料题]")
    private QuestionTypeEnum questionType;

    @Schema(title = "维度ID")
    private String dimensionId;

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

    @Schema(title = "选项和答案集合")
    private List<QuestionOptionWithAnswerResponse> optionWithAnswerList;

    @Schema(title = "子问题")
    private List<QuestionResponse> children;

//    @Override
//    public QuestionResponse clone() {
//        try {
//            QuestionResponse clone = (QuestionResponse) super.clone();
//
//            List<QuestionOptionWithAnswerResponse> optionWithAnswerList = new ArrayList<>();
//            for (QuestionOptionWithAnswerResponse qwa : this.optionWithAnswerList) {
//                optionWithAnswerList.add(qwa.clone());
//            }
//            clone.setOptionWithAnswerList(optionWithAnswerList);
//
//            List<QuestionResponse> children = new ArrayList<>();
//            for (QuestionResponse qr : this.children) {
//                children.add(qr.clone());
//            }
//            clone.setChildren(children);
//
//            return clone;
//        } catch (CloneNotSupportedException e) {
//            throw new AssertionError();
//        }
//    }
}
