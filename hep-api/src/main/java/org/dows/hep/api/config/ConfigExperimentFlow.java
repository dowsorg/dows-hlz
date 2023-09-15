package org.dows.hep.api.config;

/**
 * @author : wuzl
 * @date : 2023/8/25 17:27
 */
public class ConfigExperimentFlow {
    /**
     * 是否使用新实验流程
     */
    public static final boolean SWITCH2SysEvent=true;
    /**
     * 是否使用原实验流程
     */
    public static final boolean SWITCH2TaskSchedule=!SWITCH2SysEvent;

    /**
     * 使用指标计算缓存
     */
    public static final boolean SWITCH2EvalCache=true;

    /**
     * 使用指标公式缓存
     */
    public static boolean SWITCH2SpelCache=false;
}
