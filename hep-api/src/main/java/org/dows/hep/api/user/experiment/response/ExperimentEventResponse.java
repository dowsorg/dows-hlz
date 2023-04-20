package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "ExperimentEvent 对象", title = "实验突发事件")
public class ExperimentEventResponse{
    @Schema(title = "案例事件id")
    private String caseEventId;

    @Schema(title = "事件名称")
    private String caseEventName;

    @Schema(title = "事件说明")
    private String descr;

    @Schema(title = "触发类型 1-事件触发 2-条件触发")
    private Integer triggerType;

    @Schema(title = "事件选项json")
    private String actions;


}
