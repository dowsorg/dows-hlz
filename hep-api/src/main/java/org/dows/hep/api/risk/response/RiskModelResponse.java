package org.dows.hep.api.risk.response;

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
@Schema(name = "RiskModel 对象", title = "风险模型")
public class RiskModelResponse{
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String riskCategoryId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "风险模型ID")
    private String riskModelId;

    @Schema(title = "模型名称")
    private String modelName;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "死亡模型列表")
    private String ListRiskDeathModel;


}
