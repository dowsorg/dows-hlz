package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : wuzl
 * @date : 2023/8/1 15:27
 */
@AllArgsConstructor
@Getter

public enum EnumExperimentOrgReportFlag {

    REGISTRATION(1,"挂号结束报告"),
    FOLLOWUP(2,"检测随访报告")
    ;

    private Integer code;
    private String descr;
}
