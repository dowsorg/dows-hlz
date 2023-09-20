package org.dows.hep.api.enums;

public enum EnumWebSocketType {
    // 实验-方案设计-开始
    EXPT_SCHEME_START,

    // 实验-方案设计-同步
    EXPT_SCHEME_SYNC,

    // 实验-方案设计-已提交
    EXPT_SCHEME_SUBMITTED,

    // 开始实验倒计时
    START_EXPERIMENT_COUNTDOWN,
    //突发事件触发
    EVENT_TRIGGERED,
    //随访计划
    FOLLOWUP_PLAN,
    // 实验暂停
    EXPT_SUSPEND,


    EXPT_RESTART,

    //region 实验流程
    //沙盘准备倒计时，替换 START_EXPERIMENT_COUNTDOWN
    FLOW_SAND_READY,
    //沙盘期初开始
    FLOW_PERIOD_START,
    //期数翻转倒计时
    FLOW_PERIOD_ENDING,
    //期数翻转计算完毕 替换 calc
    FLOW_PERIOD_ENDED,

    //沙盘整体结束
    FLOW_SAND_END,
    //endregion
    ;
}
