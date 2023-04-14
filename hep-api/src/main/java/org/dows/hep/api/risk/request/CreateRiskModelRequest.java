package org.dows.hep.api.risk.request;

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
@Schema(name = "CreateRiskModel 对象", title = "创建风险模型对象")
public class CreateRiskModelRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "风险类别分布式ID")
    private String riskCategoryId;

    @Schema(title = "风险模型名称")
    private String name;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "创建死亡模型列表")
    private String ListCreateRiskDeathModel;


}
