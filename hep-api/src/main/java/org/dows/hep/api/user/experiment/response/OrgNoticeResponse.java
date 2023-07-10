package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;

import java.util.Date;
import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "OrgNotice 对象", title = "机构通知信息")
public class OrgNoticeResponse{
    @Schema(title = "机构通知id")
    private String experimentOrgNoticeId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "人物名称")
    private String personName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "游戏内天数")
    private Integer gameDay;

    @Schema(title = "通知时间")
    private Date noticeTime;

    @Schema(title = "通知类型 1-人物转移 2-检测随访 3-突发事件")
    private Integer noticeSrcType;

    @Schema(title = "通知标题")
    private String title;

    @Schema(title = "通知内容")
    private String content;

    @Schema(title = "操作提示")
    private String tips;

    @Schema(title = "通知状态，0-未读 1-已读")
    private Integer readState;

    @Schema(title = "处理状态，0-无需处理 1-待处理 2-已处理")
    private Integer actionState;

    @Schema(title = "突发事件处理措施列表")
    private List<ExptOrgNoticeActionVO> actions;

}
