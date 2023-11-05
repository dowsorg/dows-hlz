package org.dows.hep.api.base.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

@Getter
@AllArgsConstructor
public enum QuestionESCEnum implements StatusCode {
    PARAMS_NON_NULL(42000, "请求参数不能为空"),
    DATA_NULL(42001, "数据不存在"),
    CANNOT_DEL_REF_DATA(42002, "被引用数据不可删除"),
    QUESTION_TYPE_NON_NULL(42100, "题目类型不能为空"),
    QUESTION_TYPE_CANNOT_CHANGE(42101, "题目类型不能变更"),
    QUESTION_CLONED_OBJ_NON_NULL(42102, "被克隆对象不能为空"),
    QUESTION_CLONED_ID_NON_NULL(42103, "被克隆对象ID不能为空"),
    QUESTION_CLONED_IDENTIFIER_NON_NULL(42104, "被克隆对象标识符不能为空"),
    QUESTION_SECTION_GENERATION_MODE_NON_NULL(43000, "试卷生成方式不能为空"),
    QUESTION_SECTION_DIMENSION_SCORE_RANGE_ERROR(43001, "试卷维度分值范围错误"),
    QUESTION_ASSIGNED_DEL_REF_DATA(42003,"已经分配的试卷不能删除"),


    ;

    private final Integer code;
    private final String descr;
}
