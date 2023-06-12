package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * @author jx
 * @date 2023/5/8 10:38
 */
@Getter
@AllArgsConstructor
public enum EnumExperimentParticipator implements StatusCode {
    PARTICIPATOR_NOT_EXIST_EXCEPTION(40000,"参数不正确"),
    PARTICIPATOR_NUMBER_CANNOT_MORE_THAN_ORG_EXCEPTION(40001,"组员数不能小于小组机构数");

    private final Integer code;
    private final String descr;
}
