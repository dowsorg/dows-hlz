package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : wuzl
 * @date : 2023/7/7 10:47
 */
@AllArgsConstructor
@Getter
public enum EnumExperimentOrgNoticeType {
    TRANSFERPerson(1,"人员转移"),
    FOLLOWUP(2,"监测随访"),
    EVENTTriggered(3,"突发事件"),
    ;

    private Integer code;
    private String descr;


}
