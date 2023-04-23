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
@Schema(name = "OrgNotice 对象", title = "机构通知信息")
public class OrgNoticeResponse{
    @Schema(title = "机构通知id")
    private String experimentOrgNoticeId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "账号名称")
    private String caseAccountName;

    @Schema(title = "通知类型 1-人物转移 2-检测随访 3-突发事件")
    private Integer noticeType;

    @Schema(title = "通知来源id，转移，随访操作id，事件id")
    private String noticeSrcId;

    @Schema(title = "通知内容")
    private String content;


}
