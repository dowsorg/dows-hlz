package org.dows.hep.api.base.risk.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RiskModel 对象", title = "筛选风险模型")
public class RiskModelResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "风险模型ID")
  private String riskModelId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "风险模型类别")
  private RiskCategoryResponseRs riskCategoryResponseRs;

  @Schema(title = "模型名称")
  private String name;

  @Schema(title = "死亡概率")
  private Integer riskDeathProbability;

  @Schema(title = "0-禁用，1-启用")
  private Integer status;

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "死亡模型列表")
  private List<RiskDeathModelResponseRs> riskDeathModelResponseRsList;
}
