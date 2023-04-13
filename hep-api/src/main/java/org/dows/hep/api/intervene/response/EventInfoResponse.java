package org.dows.hep.api.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "EventInfo 对象", title = "事件信息")
public class EventInfoResponse{
    @Schema(title = "分布式id")
    private String eventId;

    @Schema(title = "突发事件名称")
    private String eventName;

    @Schema(title = "图片")
    private String pic;

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

    @Schema(title = "创建者账号")
    private String createAccountId;

    @Schema(title = "创建者名称")
    private String createAccountName;

    @Schema(title = "触发类型 1-事件触发 2-条件触发")
    private Integer triggerType;

    @Schema(title = "触发期数")
    private String triggerPeriod;

    @Schema(title = "触发时间段 1-前期 2-中期 3-后期")
    private String triggerSpan;

    @Schema(title = "事件条件json")
    private String eval;

    @Schema(title = "处理选项json")
    private String actions;


}
