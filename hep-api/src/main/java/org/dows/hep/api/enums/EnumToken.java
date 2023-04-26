package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jx
 * @date 2023/4/26 10:39
 */
@AllArgsConstructor
@Getter
public enum EnumToken {
    PROPERTIES_JWT_KEY(0, "findsoft.token.jwtkey"),
    ;
    private final Integer code;
    private final String str;
}
