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
@Schema(name = "IndicatorViewPhysicalExam 对象", title = "查看指标体格检查类")
public class IndicatorViewPhysicalExamRequest{
    @Schema(title = "查看指标体格检查类分布式Id")
    private String IndicatorViewPhysicalExamId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
