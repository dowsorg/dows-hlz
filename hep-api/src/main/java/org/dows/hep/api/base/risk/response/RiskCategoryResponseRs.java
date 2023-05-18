package org.dows.hep.api.base.risk.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
@Schema(name = "RiskCategoryResponseRs对象", title = "筛选风险类别")
public class RiskCategoryResponseRs {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "分布式ID")
  private String riskCategoryId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "父ID")
  private String pid;

  @Schema(title = "风险类别名称")
  private String riskCategoryName;

  @Schema(title = "展示顺序")
  private Integer seq;

  @Schema(title = "时间戳")
  private Date dt;
}
