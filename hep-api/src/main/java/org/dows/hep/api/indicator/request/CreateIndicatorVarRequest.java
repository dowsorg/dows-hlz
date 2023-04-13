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
@Schema(name = "CreateIndicatorVar 对象", title = "创建指标变量对象")
public class CreateIndicatorVarRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "数据库名")
    private String dbName;

    @Schema(title = "表名")
    private String tbName;

    @Schema(title = "变量名")
    private String varName;

    @Schema(title = "变量code")
    private String varCode;

    @Schema(title = "期数，如果多期用[,]分割")
    private String periods;

    @Schema(title = "描述")
    private String descr;


}
