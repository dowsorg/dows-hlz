package org.dows.hep.api.base.intervene.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@Schema(name = "SaveEvent 对象", title = "保存事件")
public class SaveEventRequest{

    @Schema(title = "应用ID")
    private String appId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;
    @Schema(title = "分布式id,新增时为空")
    private String eventId;

    @Schema(title = "突发事件名称")
    private String eventName;



    @Schema(title = "分类id")
    private String eventCategId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "事件描述")
    private String descr;


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
