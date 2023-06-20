package org.dows.hep.api.base.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fhb
 * @description 针对问题域和问题集域均有效
 * @date 2023/4/25 17:48
 */
@AllArgsConstructor
@Getter
public enum QuestionEnabledEnum {
    ENABLED(1, "启用"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String name;
}
