package org.dows.hep.biz.event.sysevent.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.ISysEventDealer;
import org.dows.hep.biz.event.sysevent.dealers.*;
import org.dows.hep.entity.ExperimentSysEventEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/8/23 13:31
 */

@Getter
@RequiredArgsConstructor
public enum EnumSysEventDealType {
    NONE(0,"NA",0,null),
    EXPERIMENTStart(1,"实验开始",1, ExperimentStartDealer.class),

    EXPERIMENTReady(2,"倒计时进入",2,ExperimentReadyDealer.class),
    SCHEMAStart(11,"方案设计开始",11, SchemaStartDealer.class),
    SCHEMAGroupEnd(12,"方案设计小组结束",12, SchemaGroupEndDealer.class),
    SCHEMAEnd(13,"方案设计整体结束",13, SchemaEndDealer.class),

    SANDStart(21,"沙盘开始",21, SandStartDealer.class),
    PERIODStart(22,"单期开始",22, PeriodStartDealer.class),
    PERIODEnd(23,"单期结束",23, PeriodEndDealer.class),
    EXPERIMENTReport(99,"实验报告",99,ExperimentReportDealer.class),

    ;
    private final Integer code;
    private final String name;

    //处理顺序
    private final int dealSeq;
    private final Class<? extends ISysEventDealer> dealerClazz;

    public ISysEventDealer getDealer(){
        if(null== dealerClazz){
            return null;
        }
        return CrudContextHolder.getBean(dealerClazz);
    }

    public boolean dealEvent(SysEventRow row, SysEventRunStat stat)  {
        return Optional.ofNullable(getDealer())
                .map(i->i.dealEvent(row,stat))
                .orElse(false);
    }
    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl){
        return Optional.ofNullable(getDealer())
                .map(i->i.buildEvents(exptColl))
                .orElse(null);
    }

    public static EnumSysEventDealType of(Integer code){
        return Arrays.stream(EnumSysEventDealType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(EnumSysEventDealType.NONE);
    }


}
