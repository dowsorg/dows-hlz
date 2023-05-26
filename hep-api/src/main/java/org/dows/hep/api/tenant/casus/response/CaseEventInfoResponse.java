package org.dows.hep.api.tenant.casus.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.EventActionVO;
import org.dows.hep.api.base.intervene.vo.EventEvalVO;
import org.dows.hep.api.base.intervene.vo.EventIndicatorVO;

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
@Schema(name = "CaseEventInfo 对象", title = "事件信息")
public class CaseEventInfoResponse{

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String caseEventId;
    @Schema(title = "案例id")
    private String caseInstanceId;

    @Schema(title = "人物accountId 数据库/案例人物")
    private String personId;

    @Schema(title = "人物名称 数据库/案例人物")
    private String personName;

    @Schema(title = "数据库事件id")
    private String eventId;

    @Schema(title = "事件名称")
    private String caseEventName;


    @Schema(title = "分类id")
    private String eventCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "事件说明")
    private String descr;

    @Schema(title = "事件提示")
    private String tips;

    @Schema(title = "创建者账号")
    private String createAccountId;

    @Schema(title = "创建者名称")
    private String createAccountName;

    @Schema(title = "触发类型 0-条件触发 1-第一期 2-第二期...5-第5期")
    private Integer triggerType;



    @Schema(title = "触发时间段 1-前期 2-中期 3-后期")
    private String triggerSpan;

    @Schema(title = "事件影响指标列表,仅限时间触发triggerType>0时有值")
    private List<EventIndicatorVO> indicators;
    @Schema(title = "事件触发条件列表,仅限条件触发triggerType=0时有值")
    private List<EventEvalVO> evals;

    @Schema(title = "事件处理措施列表")
    private List<EventActionVO> actions;


}
