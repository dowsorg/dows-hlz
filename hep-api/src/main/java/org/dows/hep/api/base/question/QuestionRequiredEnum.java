package org.dows.hep.api.base.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuestionRequiredEnum {
    REQUIRED(0, "必填"),
    NOT_REQUIRED(1, "非必填");

    private final Integer code;
    private final String name;
}
