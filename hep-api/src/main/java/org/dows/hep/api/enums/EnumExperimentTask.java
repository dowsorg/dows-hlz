package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jx
 * @date 2023/7/15 16:15
 */
@AllArgsConstructor
@Getter
public enum EnumExperimentTask {
    experimentBeginTask(0, "experimentBeginTask"),
    experimentFinishTask(1, "experimentFinishTask"),
    experimentCalcTask(2, "experimentCalcTask"),
    experimentPeriodStartNoticeTask(3, "experimentPeriodStartNoticeTask"),
    experimentPeriodEndNoticeTask(4, "experimentPeriodEndNoticeTask"),
    ;
    private final Integer code;
    private final String desc;
}
