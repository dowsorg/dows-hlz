package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fhb
 * @version 1.0
 * @description 案例分享
 * @date 2023/7/4 9:21
 **/
@Getter
@AllArgsConstructor
public enum CaseSharedEnum {

    PRIVATE(0, "私有"),
    SHARED(1, "分享");

    private final Integer code;
    private final String name;
}
