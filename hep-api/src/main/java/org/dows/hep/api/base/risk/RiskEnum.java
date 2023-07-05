package org.dows.hep.api.base.risk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * @author jx
 * @date 2023/6/27 9:32
 */
@Getter
@AllArgsConstructor
public enum RiskEnum implements StatusCode {
    CROWDS_CAN_NOT_REPEAT(40010, "人群类别名称不能重复"),
    ;
    private final Integer code;
    private final String descr;
}
