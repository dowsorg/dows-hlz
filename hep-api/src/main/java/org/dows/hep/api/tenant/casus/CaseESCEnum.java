package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

@Getter
@AllArgsConstructor
public enum CaseESCEnum implements StatusCode {
    PARAMS_NON_NULL(42000, "请求参数不能为空"),
    DATA_NULL(42001, "数据不存在"),
    CANNOT_DEL_REF_DATA(42002, "被引用数据不可删除"),
    CASE_ORG_NON_NULL(42010, "机构数据不能为空"),
    CASE_QUESTIONNAIRE_NON_NULL(42011, "案例下问卷不能为空"),


    ;
    private final Integer code;
    private final String descr;
}
