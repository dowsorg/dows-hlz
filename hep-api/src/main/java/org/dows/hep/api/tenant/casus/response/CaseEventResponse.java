package org.dows.hep.api.tenant.casus.response;

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
@Schema(name = "CaseEvent 对象", title = "人物事件列表")
public class CaseEventResponse{
    @Schema(title = "人物事件id")
    private String caseEventId;

    @Schema(title = "突发事件名称")
    private String caseEventName;

    @Schema(title = "一级分类id")
    private String eventCategIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "创建者账号")
    private String createAccountId;

    @Schema(title = "创建者名称")
    private String createAccountName;

    @Schema(title = "触发类型 1-事件触发 2-条件触发")
    private Integer triggerType;

    @Schema(title = "触发类型描述")
    private String triggerTypeDescr;


}
