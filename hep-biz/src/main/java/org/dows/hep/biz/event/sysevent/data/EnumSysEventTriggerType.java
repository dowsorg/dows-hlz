package org.dows.hep.biz.event.sysevent.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.ISysEventTrigger;
import org.dows.hep.biz.event.sysevent.triggers.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/8/23 13:31
 */
@AllArgsConstructor
@Getter
public enum EnumSysEventTriggerType {
    MANUAL(0, "手动触发", null),
    EXPERIMENTStart(1, "实验开始", ExperimentStartTrigger.class),

    EXPERIMENTReady(2, "倒计时进入", ExperimentReadyTrigger.class),
    SCHEMAStart(11, "方案设计开始", SchemaStartTrigger.class),
    SCHEMAGroupEnd(12,"方案设计小组结束", SchemaGroupEndTrigger.class),
    SCHEMAEnd(13, "方案设计整体结束", SchemaEndTrigger.class),
//    SCHEMAScoreEnd(14,"方案设计评分结束",14, SchemaScoreEndTrigger.class),
    SCHEMAAuditEnd(15,"方案设计评分审核结束", SchemaAuditEndTrigger.class),


    SANDStart(21, "沙盘开始", SandStartTrigger.class),
    PERIODStart(22, "单期开始", PeriodStartTrigger.class),
    PERIODEnd(23, "单期结束", PeriodEndTrigger.class),
    EXPERIMENTReport(99, "实验报告", ExperimentReportTrigger.class),
    ;
    private Integer code;
    private String name;

    private final Class<? extends ISysEventTrigger> triggerClazz;

    public ISysEventTrigger getTrigger() {
        if (null == triggerClazz) {
            return null;
        }
        return CrudContextHolder.getBean(triggerClazz);
    }

    public ExperimentTimePoint getTriggerTime(ExperimentSettingCollection exptColl, Integer period, long cntPauseSeconds) {
        return Optional.ofNullable(getTrigger())
                .map(i -> i.getTriggerTime(exptColl, period, cntPauseSeconds))
                .orElse(null);
    }


    public static EnumSysEventTriggerType of(Integer code) {
        return Arrays.stream(EnumSysEventTriggerType.values())
                .filter(i -> i.getCode().equals(code))
                .findFirst()
                .orElse(EnumSysEventTriggerType.MANUAL);
    }
}
