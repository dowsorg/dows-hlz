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
@Schema(name = "FollowupState 对象", title = "随访状态")
public class FollowupStateResponse{
    @Schema(title = "随访操作状态id")
    private String operateFollowupTimerId;

    @Schema(title = "随访表id")
    private String indicatorViewMonitorFollowupId;

    @Schema(title = "随访表名称")
    private String indicatorFollowupName;

    @Schema(title = "随访间隔天数")
    private Integer dueDays;

    @Schema(title = "可以随访时间，游戏时间未到该天数不可随访")
    private Integer todoDay;

    @Schema(title = "是否可保存随访，0-否 1-是")
    private Integer enableSet;

    @Schema(title = "是否可执行随访，0-否 1-是")
    private Integer enableExec;


}
