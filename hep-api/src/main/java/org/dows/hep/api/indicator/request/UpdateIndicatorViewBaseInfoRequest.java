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
@Schema(name = "UpdateIndicatorViewBaseInfo 对象", title = "更改指标基本信息类")
public class UpdateIndicatorViewBaseInfoRequest{
    @Schema(title = "指标基本信息类名称")
    private String name;

    @Schema(title = "")
    private  指标描述表列表;

    @Schema(title = "")
    private  指标监测表列表;

    @Schema(title = "")
    private  单一指标列表;


}
