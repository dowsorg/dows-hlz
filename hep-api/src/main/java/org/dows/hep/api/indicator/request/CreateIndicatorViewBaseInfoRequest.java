package org.dows.hep.api.indicator.request;

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
@Schema(name = "CreateIndicatorViewBaseInfo 对象", title = "创建指标基本信息类")
public class CreateIndicatorViewBaseInfoRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标基本信息类名称")
    private String name;

    @Schema(title = "")
    private  指标描述表列表;

    @Schema(title = "")
    private  指标监测表列表;

    @Schema(title = "")
    private  单一指标列表;


}
