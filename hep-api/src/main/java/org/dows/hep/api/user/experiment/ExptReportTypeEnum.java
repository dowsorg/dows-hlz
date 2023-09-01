package org.dows.hep.api.user.experiment;

/**
 * @author fhb
 * @version 1.0
 * @description 报告类型枚举
 * @date 2023/7/21 11:56
 **/
public enum ExptReportTypeEnum {

    // 实验总报告
    EXPT,

    // 实验报告压缩包（实验总报告+所有小组报告）
    EXPT_ZIP,

    // 小组实验报告
    GROUP,

    // 小组报告压缩包（小组实验报告+实验总报告）
    GROUP_ZIP,

    // 学生报告
    ACCOUNT,

    // 学生报告压缩包
    ACCOUNT_ZIP;
}
