package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExptQuestionnaireStateEnum {
    NOT_STARTED(0, "未开始"),
    UNDER_WAY(1, "进行中"),
    SUBMITTED(2, "已提交"),
    ;

    private final Integer code;
    private final String name;
}
